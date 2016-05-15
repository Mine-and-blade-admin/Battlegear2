 package org.bukkit.event;
 
 public abstract class Event
 {
   private final boolean async;
 
   public Event()
   {
     this(false);
   }
 
   public Event(boolean isAsync)
   {
     this.async = isAsync;
   }
 
   public static enum Result
   {
     DENY, 
 
     DEFAULT, 
 
     ALLOW;
   }
 }