package l2r.gameserver.model.actor.instance;

import l2r.gameserver.data.xml.impl.MultisellData;
import l2r.gameserver.enums.InstanceType;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import gr.sr.main.Conditions;

/**
 * @author VMILON
 */
public class L2LoyaltyInstance extends L2Npc
{
	public L2LoyaltyInstance(L2NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2LoyaltyInstance);
	}
	
	/**
	 * Method to send the html to the player
	 * @param player
	 * @param html
	 */
	public void sendPacket(L2PcInstance player, String html)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
		msg.setFile(player, player.getHtmlPrefix(), "/data/html/SoE/LoyaltyShop/" + html);
		msg.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(msg);
	}
	
	@Override
	public void showChatWindow(L2PcInstance player)
	{
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		if (player.isPremium() || player.isPlatinum())
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player, player.getHtmlPrefix(), "data/html/SoE/LoyaltyShop/main.htm");
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player, player.getHtmlPrefix(), "data/html/SoE/LoyaltyShop/noprem.htm");
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		
	}
	
	@Override
	public void onBypassFeedback(final L2PcInstance player, String command)
	{
		final String[] subCommand = command.split("_");
		
		// No null pointers
		if (player == null)
		{
			return;
		}
		
		// Restrictions Section
		if (!Conditions.checkPlayerBasicConditions(player))
		{
			return;
		}
		
		// Page navigation, html command how to starts
		if (command.startsWith("Chat"))
		{
			if (subCommand[1].isEmpty() || (subCommand[1] == null))
			{
				player.sendMessage("Multisell" + subCommand[1] + "COMING SOON!");
				return;
			}
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player, player.getHtmlPrefix(), "data/html/SoE/LoyaltyShop/" + subCommand[1]);
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		
		else if (command.startsWith("showMultiSellWindow"))
		{
			try
			{
				int multi = Integer.valueOf(subCommand[1]);
				MultisellData.getInstance().separateAndSend(multi, player, null, false);
			}
			catch (Exception e)
			{
				player.sendMessage("Coming Soon!");
			}
			
		}
	}
	
}
