package ai.npc.AioSigns;

import l2r.Config;
import l2r.gameserver.SevenSigns;
import l2r.gameserver.enums.QuestType;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.network.SystemMessageId;

import ai.npc.AbstractNpcAI;

/**
 * @author VMILON
 */
public final class AioSigns extends AbstractNpcAI
{
	
	// NPC
	private static final int MAMMON_FORGE = 523;
	
	// Misc
	private static final int MIN_LEVEL = 60;
	private static final double WEIGHT_LIMIT = 0.80;
	
	public AioSigns()
	{
		super(AioSigns.class.getSimpleName(), "ai/npc");
		addStartNpc(MAMMON_FORGE);
		addFirstTalkId(MAMMON_FORGE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.endsWith(".htm"))
		{
			return "AioSigns" + event;
		}
		
		if (event.startsWith("blacksmith"))
		{
			final int compWinner = SevenSigns.getInstance().getCabalHighestScore();
			final int sealGnosisOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS);
			final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player.getObjectId());
			
			if (Config.ALT_STRICT_SEVENSIGNS)
			{
				switch (compWinner)
				{
					case SevenSigns.CABAL_DAWN:
						if ((playerCabal != compWinner) || (playerCabal != sealGnosisOwner))
						{
							return "blacksmith-no.htm";
						}
						break;
					case SevenSigns.CABAL_DUSK:
						if ((playerCabal != compWinner) || (playerCabal != sealGnosisOwner))
						{
							return "blacksmith-no.htm";
						}
						break;
					case SevenSigns.CABAL_NULL:
						if (player.isGM())
						{
							return "blacksmith.htm";
						}
						
						return "blacksmith-no.htm";
						
				}
			}
			
			return "blacksmith.htm";
		}
		
		if (event.startsWith("marketeer"))
		{
			return "marketeer.htm";
		}
		if (event.startsWith("merchant"))
		{
			final int compWinner = SevenSigns.getInstance().getCabalHighestScore();
			final int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
			final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player.getObjectId());
			
			if (Config.ALT_STRICT_SEVENSIGNS)
			{
				switch (compWinner)
				{
					case SevenSigns.CABAL_DAWN:
						if ((playerCabal != compWinner) || (playerCabal != sealAvariceOwner))
						{
							return "merchant-no.htm";
						}
						break;
					case SevenSigns.CABAL_DUSK:
						if ((playerCabal != compWinner) || (playerCabal != sealAvariceOwner))
						{
							return "merchant-no.htm";
						}
						break;
					case SevenSigns.CABAL_NULL:
						if (player.isGM())
						{
							return "merchant.htm";
						}
						return "merchant-no.htm";
				}
			}
			
			return "merchant.htm";
		}
		
		if (event.startsWith("exchange"))
		{
			
			long ancientAdena;
			try
			{
				ancientAdena = Long.parseLong(event.substring(9).trim());
			}
			catch (Exception e)
			{
				return "AioSigns_q0506_14.htm";
			}
			
			if (player.getAncientAdena() < ancientAdena)
			{
				return "AioSigns_q0506_12.htm";
			}
			
			if (ancientAdena <= 0)
			{
				return "AioSigns_q0506_14.htm";
			}
			
			if ((player.getInventory().getSize(false) >= (player.getInventoryLimit() * WEIGHT_LIMIT)) || (player.getCurrentLoad() >= (player.getMaxLoad() * WEIGHT_LIMIT)))
			{
				player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
				return null;
			}
			takeItems(player, Inventory.ANCIENT_ADENA_ID, ancientAdena);
			giveItems(player, Inventory.ADENA_ID, ancientAdena);
			return "AioSigns_q0506_13.htm";
			
		}
		
		int ask = Integer.parseInt(event.split(";")[0]);
		int reply = Integer.parseInt(event.split(";")[1]);
		switch (ask)
		{
			case 506:
			{
				switch (reply)
				{
					case 3:
					case 4:
					case 5:
					{
						return "AioSigns_q0506_04.htm";
					}
				}
				break;
			}
			case 989:
			{
				switch (reply)
				{
					case 3:
					{
						/*
						 * if (!exchangeAvailable(player)) { long remainingTimeMillis = getExchangeRemainingTime(player); long hours = remainingTimeMillis / 3600000; long minutes = (remainingTimeMillis % 3600000) / 60000; String text = "You need to wait " + hours + " hour(s) and " + minutes +
						 * " minute(s) before using the exchange again."; player.sendMessageS(text, 6); return "AioSignsmark002e.htm"; }
						 */
						return "AioSignsmark003.htm";
						
					}
				}
				break;
			}
			case 990:
			{
				switch (reply)
				{
					case 3:
					{
						if (player.getAdena() < 2000000)
						{
							return "AioSignsmark002c.htm";
						}
						
						final QuestState qs = getQuestState(player, true);
						if (!qs.isNowAvailable())
						{
							return "AioSignsmark002b.htm";
						}
						
						if (player.getLevel() < MIN_LEVEL)
						{
							return "AioSignsmark002d.htm";
						}
						
						if ((player.getInventory().getSize(false) >= (player.getInventoryLimit() * WEIGHT_LIMIT)) || (player.getCurrentLoad() >= (player.getMaxLoad() * WEIGHT_LIMIT)))
						{
							player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
							return null;
						}
						
						qs.setState(State.STARTED);
						takeItems(player, Inventory.ADENA_ID, 2000000);
						giveItems(player, Inventory.ANCIENT_ADENA_ID, 500000);
						qs.exitQuest(QuestType.DAILY, false);
						
						return "AioSignsmark004.htm";
					}
				}
				break;
			}
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "AioSigns.htm";
	}
}
