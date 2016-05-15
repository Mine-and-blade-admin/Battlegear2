package org.bukkit.event.player;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;

public class PlayerInteractEvent
{
  protected Action action;
  private Event.Result useClickedBlock;
  private Event.Result useItemInHand;

  public PlayerInteractEvent()
  {
     this.useItemInHand = Event.Result.ALLOW;
     this.useClickedBlock = Event.Result.DENY;
  }

  public Action getAction()
  {
     return this.action;
  }

  public boolean isCancelled()
  {
     return useInteractedBlock() == Event.Result.DENY;
  }

  public Event.Result useInteractedBlock()
  {
     return this.useClickedBlock;
  }

  public Event.Result useItemInHand()
  {
     return this.useItemInHand;
  }
}