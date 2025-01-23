package gr.sr.premiumEngine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import l2r.L2DatabaseFactory;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import gr.sr.configsEngine.configs.impl.PremiumServiceConfigs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vGodFather edited by VMILON (Split premium into gold and platinum, also added the ability to ADD time on existing gold or platinum accounts.)
 */
public class PremiumHandler
{
	public static final Logger _log = LoggerFactory.getLogger(PremiumHandler.class);
	
	// Character PremiumService String Definitions:
	private static final String INSERT_PREMIUMSERVICE = "INSERT INTO characters_premium (account_name,premium_service,enddate) values(?,?,?) ON DUPLICATE KEY UPDATE premium_service = ?, enddate = ?";
	private static final String RESTORE_PREMIUMSERVICE = "SELECT premium_service,enddate FROM characters_premium WHERE account_name=?";
	private static final String UPDATE_PREMIUMSERVICE = "INSERT INTO characters_premium (account_name,premium_service,enddate) values(?,?,?) ON DUPLICATE KEY UPDATE premium_service = ?, enddate = ?";
	private static long _end_pr_date;
	
	private static void createPSdb(L2PcInstance activeChar)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_PREMIUMSERVICE))
		{
			statement.setString(1, activeChar._accountName);
			statement.setInt(2, 0);
			statement.setLong(3, 0);
			statement.setInt(4, 0);
			statement.setLong(5, 0);
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			_log.error(PremiumHandler.class.getSimpleName() + ": Could not insert char data: " + e);
		}
	}
	
	private static void PStimeOver(String account)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_PREMIUMSERVICE))
		{
			statement.setString(1, account);
			statement.setInt(2, 0);
			statement.setLong(3, 0);
			statement.setInt(4, 0);
			statement.setLong(5, 0);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error(PremiumHandler.class.getSimpleName() + ": Could not increase data: " + e);
		}
	}
	
	public static long getPremServiceData(String playerAcc)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT premium_service,enddate FROM characters_premium WHERE account_name=?"))
		{
			statement.setString(1, playerAcc);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					if (PremiumServiceConfigs.USE_PREMIUM_SERVICE)
					{
						_end_pr_date = rset.getLong("enddate");
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.error(PremiumHandler.class.getSimpleName() + ": Could not increase data: " + e);
		}
		return _end_pr_date;
	}
	
	public static void restorePremServiceData(L2PcInstance player, String account)
	{
		boolean sucess = false;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_PREMIUMSERVICE))
		{
			statement.setString(1, account);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					sucess = true;
					if (PremiumServiceConfigs.USE_PREMIUM_SERVICE)
					{
						if (rset.getLong("enddate") <= System.currentTimeMillis())
						{
							PStimeOver(account);
							player.setPremiumService(false);
						}
						else if ((rset.getInt("premium_service") == 1) && (rset.getLong("enddate") > System.currentTimeMillis()))
						{
							player.setPremiumService(true);
							player.setPlatinumService(false);
						}
						else if ((rset.getInt("premium_service") == 2) && (rset.getLong("enddate") > System.currentTimeMillis()))
						{
							player.setPremiumService(false);
							player.setPlatinumService(true);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.error(PremiumHandler.class.getSimpleName() + ": Could not restore PremiumService data for:" + account + "." + e);
		}
		if (!sucess)
		{
			createPSdb(player);
			player.setPremiumService(false);
		}
	}
	
	public static void addPremiumServices(int time, L2PcInstance player)
	{
		addPremiumServices(time, player.getAccountName(), PremiumDuration.WEEKS, player);
	}
	
	public static void addPremiumServices(int time, String accName, PremiumDuration duration, L2PcInstance player)
	{
		
		Calendar finishtime = Calendar.getInstance();
		finishtime.setTimeInMillis(System.currentTimeMillis());
		
		switch (duration)
		{
			case DAYS:
				finishtime.add(Calendar.DAY_OF_MONTH, time);
				break;
			case WEEKS:
				finishtime.add(Calendar.WEEK_OF_YEAR, time);
				break;
			case MONTHS:
				finishtime.add(Calendar.MONTH, time);
				break;
			case YEARS:
				finishtime.add(Calendar.YEAR, time);
				break;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_PREMIUMSERVICE);
			PreparedStatement checkStatement = con.prepareStatement(RESTORE_PREMIUMSERVICE))
		{
			checkStatement.setString(1, accName);
			try (ResultSet rset = checkStatement.executeQuery())
			{
				if (rset.next())
				{
					int currentPremiumStatus = rset.getInt("premium_service");
					long currentEndDate = rset.getLong("enddate");
					
					if ((currentPremiumStatus == 1) && (currentEndDate > System.currentTimeMillis())) // Gold
					{
						finishtime.setTimeInMillis(currentEndDate);
						if (time == 4)
						{
							finishtime.add(Calendar.DAY_OF_MONTH, 30);
							
						}
						else if (time == 6)
						{
							finishtime.add((Calendar.HOUR_OF_DAY), 6);
						}
						else
						{
							finishtime.add(Calendar.WEEK_OF_YEAR, time);
						}
						
					}
					else if ((currentPremiumStatus == 2) && (currentEndDate > System.currentTimeMillis()))
					{
						player.sendMessage("You are currently a platinum member. Platinum members cannot downgrade!");
						return;
					}
				}
			}
			
			statement.setString(1, accName);
			statement.setInt(2, 1);
			statement.setLong(3, finishtime.getTimeInMillis());
			statement.setInt(4, 1);
			statement.setLong(5, finishtime.getTimeInMillis());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error(PremiumHandler.class.getSimpleName() + ": Could not increase data." + e);
		}
	}
	
	public static void addPlatinumServices(int time, L2PcInstance player)
	{
		addPlatinumServices(time, player.getAccountName(), PremiumDuration.WEEKS, player);
	}
	
	// public static void addPlatinumServices30(int time, L2PcInstance player)
	// {
	// addPlatinumServices(30, player.getAccountName(), PremiumDuration.DAYS, player);
	// }
	
	public static void addPlatinumServices(int time, String accName, PremiumDuration duration, L2PcInstance player)
	{
		
		Calendar finishtime = Calendar.getInstance();
		finishtime.setTimeInMillis(System.currentTimeMillis());
		
		switch (duration)
		{
			case HOURS:
				finishtime.add(Calendar.HOUR_OF_DAY, time);
				break;
				
			case DAYS:
				finishtime.add(Calendar.DAY_OF_MONTH, time);
				break;
			case WEEKS:
				
				finishtime.add(Calendar.WEEK_OF_YEAR, time);
				break;
				
			case MONTHS:
				finishtime.add(Calendar.MONTH, time);
				break;
			case YEARS:
				finishtime.add(Calendar.YEAR, time);
				break;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_PREMIUMSERVICE);
			PreparedStatement checkStatement = con.prepareStatement(RESTORE_PREMIUMSERVICE))
		{
			checkStatement.setString(1, accName);
			try (ResultSet rset = checkStatement.executeQuery())
			{
				if (rset.next())
				{
					int currentPremiumStatus = rset.getInt("premium_service");
					long currentEndDate = rset.getLong("enddate");
					
					if ((currentPremiumStatus == 2) && (currentEndDate > System.currentTimeMillis())) // Platinum
					{
						finishtime.setTimeInMillis(currentEndDate);
						if (time == 4)
						{
							finishtime.add(Calendar.DAY_OF_MONTH, 30);
							
						}
						
						else
						{
							finishtime.add(Calendar.WEEK_OF_YEAR, time);
						}
					}
				}
			}
			
			statement.setString(1, accName);
			statement.setInt(2, 2);
			statement.setLong(3, finishtime.getTimeInMillis());
			statement.setInt(4, 2);
			statement.setLong(5, finishtime.getTimeInMillis());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error(PremiumHandler.class.getSimpleName() + ": Could not increase data." + e);
		}
	}
	
	public static void removePremiumServices(String accName)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_PREMIUMSERVICE))
		{
			statement.setString(1, accName);
			statement.setInt(2, 0); // Set premium service to inactive
			statement.setLong(3, 0); // Set end date to 0
			statement.setInt(4, 0); // Ensure both premium_service and enddate are reset
			statement.setLong(5, 0);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error(PremiumHandler.class.getSimpleName() + ": Could not remove premium service data for account: " + accName + ". " + e);
		}
	}
	
}
