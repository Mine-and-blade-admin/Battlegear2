package mods.battlegear2.utils;

import mods.battlegear2.Battlegear;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class BattlegearCommands extends CommandBase{


    @Override
    public String getCommandName() {
        return "mb";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {

        if(astring.length > 0){
            if(astring[0].equalsIgnoreCase("latest")){
                if(Battlegear.latestRelease != null && Battlegear.latestRelease.url != null){
                    openURL(Battlegear.latestRelease.url);
                }
            }

        }

    }

    private void openURL(String url) {
        try{

            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();

            if (os.indexOf( "win" ) >= 0) {

                // this doesn't support showing urls in the form of "page.html#nameLink"
                rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);

            } else if (os.indexOf( "mac" ) >= 0) {

                rt.exec( "open " + url);

            } else if (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0) {

                // Do a best guess on unix until we get a platform independent way
                // Build a list of browsers to try, in this order.
                String[] browsers = {"chrome", "google-chrome", "firefox", "mozilla", "epiphany","konqueror",
                        "netscape","opera","links","lynx"};

                // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
                StringBuffer cmd = new StringBuffer();
                for (int i=0; i<browsers.length; i++)
                    cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + url + "\" ");

                rt.exec(new String[] { "sh", "-c", cmd.toString() });

            } else {
                return;
            }
        }catch (Exception e){
            return;
        }
    }
}
