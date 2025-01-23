package l2r.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import l2r.L2DatabaseFactory;
import l2r.gameserver.enums.InstanceType;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import gr.sr.premiumEngine.PremiumDuration;
import gr.sr.premiumEngine.PremiumHandler;

/**
 * Handles Promo Codes for players.
 * @author VMILON
 */
public class L2PromoInstance extends L2Npc
{
	private static final Logger _log = Logger.getLogger(L2PromoInstance.class.getName());
	
	public L2PromoInstance(L2NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2PromoInstance);
	}
	
	public void sendPacket(L2PcInstance player, String message)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player, player.getHtmlPrefix(), "/data/html/SoE/PromoManager/" + message);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}
	
	@Override
	public void showChatWindow(L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player, player.getHtmlPrefix(), "data/html/SoE/PromoManager/main.htm");
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}
	
	@Override
	public void onBypassFeedback(final L2PcInstance player, String command)
	{
		if (command.startsWith("usePromoCode"))
		{
			String code = command.substring(13).trim();
			handlePromoCode(player, code);
		}
	}
	
	public void handlePromoCode(L2PcInstance player, String code)
	{
		code = sanitizePromoCode(code);
		if (code.isEmpty())
		{
			player.sendMessage("Please enter a valid promo code.");
			return;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			// Check if the player has already used this promo code (1 PER ACCOUNT)
			try (PreparedStatement checkUsage = con.prepareStatement("SELECT 1 FROM promo_usage WHERE account_name = ? AND promo_code = ?"))
			{
				checkUsage.setString(1, player.getAccountName());
				checkUsage.setString(2, code);
				try (ResultSet rs = checkUsage.executeQuery())
				{
					if (rs.next())
					{
						player.sendMessage("This promo code has already been used on your account.");
						return;
					}
				}
			}
			
			// Fetch promo code details
			try (PreparedStatement ps = con.prepareStatement("SELECT * FROM promo WHERE code = ? AND active = 1"))
			{
				ps.setString(1, code);
				try (ResultSet rs = ps.executeQuery())
				{
					if (rs.next())
					{
						// Retrieve level restriction
						int minLevel = rs.getInt("min_lvl");
						
						// Check if player meets the level requirement
						if (player.getLevel() < minLevel)
						{
							player.sendMessage("You need to be at least level " + minLevel + " to use this promo code.");
							return;
						}
						
						// Retrieve promo rewards
						int[] items =
						{
							rs.getInt("item0"),
							rs.getInt("item1"),
							rs.getInt("item2"),
							rs.getInt("item3"),
							rs.getInt("item4"),
							rs.getInt("item5")
						};
						int[] amounts =
						{
							rs.getInt("amount0"),
							rs.getInt("amount1"),
							rs.getInt("amount2"),
							rs.getInt("amount3"),
							rs.getInt("amount4"),
							rs.getInt("amount5")
						};
						int goldStatus = rs.getInt("gold");
						int platinumStatus = rs.getInt("platinum");
						
						// Reward player with items and their corresponding amounts
						for (int i = 0; i < items.length; i++)
						{
							if ((items[i] > 0) && (amounts[i] > 0))
							{
								player.addItem("Promo", items[i], amounts[i], player, true);
							}
						}
						
						// Apply Gold Premium
						if (goldStatus > 0)
						{
							PremiumHandler.addPremiumServices(goldStatus, player.getAccountName(), PremiumDuration.WEEKS, player);
							player.sendMessage("You received " + goldStatus + " week(s) of Gold Account.");
						}
						
						// Apply Platinum Premium
						if (platinumStatus > 0)
						{
							PremiumHandler.addPlatinumServices(platinumStatus, player.getAccountName(), PremiumDuration.WEEKS, player);
							player.sendMessage("You received " + platinumStatus + " week(s) of Platinum Account.");
						}
						
						// Record the usage of this promo code
						try (PreparedStatement logUsage = con.prepareStatement("INSERT INTO promo_usage (account_name, char_name, promo_code) VALUES (?, ?, ?)"))
						{
							logUsage.setString(1, player.getAccountName()); // Account name
							logUsage.setString(2, player.getName()); // Character name (Extra so we can know exactly which char activated the code)
							logUsage.setString(3, code); // Promo code
							logUsage.executeUpdate();
						}
						
						// Send success message and log usage
						player.sendMessageS("Promo code applied successfully!", 5);
						
					}
					else
					{
						player.sendMessage("The code is invalid or has expired.");
					}
				}
			}
		}
		catch (SQLException e)
		{
			player.getAccountName();
			_log.warning("Error handling promo code: " + e.getMessage());
			player.sendMessage("An error occurred while processing your promo code. Please contact an admin!");
		}
	}
	
	private String sanitizePromoCode(String code)
	{
		return code.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
	}
}
