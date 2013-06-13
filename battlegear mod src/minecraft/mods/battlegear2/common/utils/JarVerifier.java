/*
 * Copyright 2004 - 2007 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mods.battlegear2.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Verifies a signed jar file given an array of truted CA certs
 *
 * @author Andrew Harrison
 * @version $Revision: 148 $
 * @created Apr 11, 2007: 11:02:26 PM
 * @date $Date: 2007-04-12 13:31:48 +0100 (Thu, 12 Apr 2007) $ modified by $Author: scmabh $
 * @todo Put your notes here...
 */


public class JarVerifier {
	
	private static byte[] key = new byte[]{
			48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122,
			-9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0, 48, -127,
			-119, 2, -127, -127, 0, -92, -120, -50, 15, -123, -51,
			-19, 114, 1, 116, -99, -68, -62, 118, -110, 53, -35, -86,
			121, -64, 92, -123, 46, -52, -117, -83, -34, 39, -102,
			121, 109, 59, 65, -10, 51, -125, -93, -43, -125, -22, 8, 
			89, -66, -58, 22, -15, -37, 26, 43, 43, -54, 7, -110, 18, 82,
			112, 127, 17, 80, 106, 65, 9, -72, -62, 22, -67, 2, -55, -6,
			-1, 15, -17, 103, 18, -105, 15, 98, -100, 85, 40, -100, 96,
			-3, 113, -56, -116, 55, -41, -21, 98, -79, -1, 21, 6, 9, -15, 
			121, -43, 113, 109, 121, -28, 101, -75, -25, 121, 93, 20, -124, 
			-103, 82, -44, -113, 52, -95, 74, -116, -101, -20, -64, -53, 44, 
			5, -83, 53, -64, 113, 117, 2, 3, 1, 0, 1
			};
    public static void verify(JarFile jf, X509Certificate[] trustedCaCerts) throws IOException, CertificateException {
        Vector<JarEntry> entriesVec = new Vector<JarEntry>();

        // Ensure there is a manifest file
        Manifest man = jf.getManifest();
        if (man == null)
            throw new SecurityException("The JAR is not signed");

        // Ensure all the entries' signatures verify correctly
        byte[] buffer = new byte[8192];
        Enumeration entries = jf.entries();

        while (entries.hasMoreElements()) {
            JarEntry je = (JarEntry) entries.nextElement();
            entriesVec.addElement(je);
            InputStream is = jf.getInputStream(je);
            int n;
            while ((n = is.read(buffer, 0, buffer.length)) != -1) {
                // we just read. this will throw a SecurityException
                // if  a signature/digest check fails.
            }
            is.close();
        }
        jf.close();

        // Get the list of signer certificates
        Enumeration e = entriesVec.elements();
        while (e.hasMoreElements()) {
            JarEntry je = (JarEntry) e.nextElement();

            if (je.isDirectory())
                continue;
            // Every file must be signed - except
            // files in META-INF
            Certificate[] certs = je.getCertificates();
            if ((certs == null) || (certs.length == 0)) {
                if (!je.getName().startsWith("META-INF"))
                    throw new SecurityException("The JCE framework " +
                            "has unsigned " +
                            "class files.");
            } else {
                // Check whether the file
                // is signed as expected.
                // The framework may be signed by
                // multiple signers. At least one of
                // the signers must be a trusted signer.

                // First, determine the roots of the certificate chains
                X509Certificate[] chainRoots = getChainRoots(certs);
                boolean signedAsExpected = false;
                
                for (Certificate c : certs) {
					X509Certificate c2 = (X509Certificate)c;
					if(Arrays.equals(c2.getPublicKey().getEncoded(), key)){
						signedAsExpected = true;
					}
				}

                if (!signedAsExpected) {
                    throw new SecurityException("The JAR is not signed by a trusted signer");
                }
            }
        }
    }

    public static boolean isTrusted(X509Certificate cert,
                                     X509Certificate[] trustedCaCerts) {
        // Return true iff either of the following is true:
        // 1) the cert is in the trustedCaCerts.
        // 2) the cert is issued by a trusted CA.

        // Check whether the cert is in the trustedCaCerts
        for (int i = 0; i < trustedCaCerts.length; i++) {
            // If the cert has the same SubjectDN
            // as a trusted CA, check whether
            // the two certs are the same.
            if (cert.getSubjectDN().equals(trustedCaCerts[i].getSubjectDN())) {
                if (cert.equals(trustedCaCerts[i])) {
                    return true;
                }
            }
        }

        // Check whether the cert is issued by a trusted CA.
        // Signature verification is expensive. So we check
        // whether the cert is issued
        // by one of the trusted CAs if the above loop failed.
        for (int i = 0; i < trustedCaCerts.length; i++) {
            // If the issuer of the cert has the same name as
            // a trusted CA, check whether that trusted CA
            // actually issued the cert.
            if (cert.getIssuerDN().equals(trustedCaCerts[i].getSubjectDN())) {
                try {
                    cert.verify(trustedCaCerts[i].getPublicKey());
                    return true;
                } catch (Exception e) {
                    // Do nothing.
                }
            }
        }

        return false;
    }

    public static X509Certificate[] getChainRoots(Certificate[] certs) {
        Vector<X509Certificate> result = new Vector<X509Certificate>(3);
        // choose a Vector size that seems reasonable
        for (int i = 0; i < certs.length - 1; i++) {
            if (!((X509Certificate) certs[i + 1]).getSubjectDN().equals(
                    ((X509Certificate) certs[i]).getIssuerDN())) {
                // We've reached the end of a chain
                result.addElement((X509Certificate) certs[i]);
            }
        }
        // The final entry in the certs array is always
        // a "root" certificate
        result.addElement((X509Certificate) certs[certs.length - 1]);
        X509Certificate[] ret = new X509Certificate[result.size()];
        result.copyInto(ret);

        return ret;
    }
}