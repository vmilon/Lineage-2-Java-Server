/**
	 * Logs donation actions to a CSV file.
	 * @author Vmilon L2SoE
	 * @param player the player who performed the action
	 * @param actionDescription the action description to log
	 */
	public static void logDonationAction(L2PcInstance player, String actionDescription)
	{
		// Specify the complete file path with .csv extension
		String filePath = "C:\\Users\\Administrator\\Desktop\\server\\game\\log\\L2SOEDonationLogs.csv";
		
		// Date format to include date and time
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String currentDateTime = dateFormat.format(new Date());
		
		// Create the log message in CSV format
		String logMessage = currentDateTime + "," + player.getName() + "," + actionDescription;
		
		try
		{
			// Check if the file already exists
			File file = new File(filePath);
			boolean fileExists = file.exists();
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) // 'true' for appending
			{
				// If the file doesn't exist, add a header row at the beginning
				if (!fileExists)
				{
					writer.write("Date and Time,Player,Action Performed");
					writer.newLine();
				}
				
				// Write the log message
				writer.write(logMessage);
				writer.newLine(); // Move to the next line after writing
			}
		}
		catch (IOException e)
		{
			System.out.println("Failed to log donation action: " + e.getMessage());
		}
	}



//THIS FUNCTION WAS USED IN EVERY ACTION THAT THE DONATION MANAGER NPC HANDLED. SOME WERE CREATED BY ME SOME WERE ALREADY IMPLEMENTED. HERE ARE SOME EXAMPLES OF MY WORK 

else if (command.startsWith("namecolor"))
		{
			String[] subCmd = command.split(" ");
			String selectedColor = subCmd[1]; // This is where the color selected by the player comes in.
			
			// Map of available colors with corresponding 1-10 values
			Map<String, Integer> colorMap = new HashMap<>();
			colorMap.put("lemon", 1);
			colorMap.put("lilac", 2);
			colorMap.put("cobalt", 3);
			colorMap.put("mint_green", 4);
			colorMap.put("peacock", 5);
			colorMap.put("ochre", 6);
			colorMap.put("chocolate", 7);
			colorMap.put("pink", 8);
			colorMap.put("rose_pink", 9);
			colorMap.put("silver", 10);
			
			// Check if the player has enough Coins of Eternia
			if (!Conditions.checkPlayerItemCount(player, CommunityDonateConfigs.NAME_COLOR_PRICE_ID, CommunityDonateConfigs.NAME_COLOR_PRICE))
			{
				player.sendMessage("You need 150 Coins of Eternia to perform this action.");
				return;
			}
			
			// Check if the selected color is valid
			if (colorMap.containsKey(selectedColor))
			{
				
				player.destroyItemByItemId("Name Color Change", CommunityDonateConfigs.NAME_COLOR_PRICE_ID, CommunityDonateConfigs.NAME_COLOR_PRICE, player, true);
				
				// Get the corresponding number for the color (1-10)
				int colorCode = colorMap.get(selectedColor);
				
				// Set the variable doncolor to this number
				player.setVar("doncolor", Integer.toString(colorCode));
				logDonationAction(player, "Name Color");
				
				// Change the player's name color based on the selected value
				int actualColor = 0;
				switch (colorCode)
				{
					case 1:
						actualColor = 0x97F8FC; // Lemon
						break;
					case 2:
						actualColor = 0xFA9AEE; // Lilac
						break;
					case 3:
						actualColor = 0xFF5D93; // Cobalt
						break;
					case 4:
						actualColor = 0x00FCA0; // Mint Green
						break;
					case 5:
						actualColor = 0xA0A601; // Peacock
						break;
					case 6:
						actualColor = 0x7898AF; // Ochre
						break;
					case 7:
						actualColor = 0x486295; // Chocolate
						break;
					case 8:
						actualColor = 0x9393FF; // Pink
						break;
					case 9:
						actualColor = 0x7C49FC; // Rose Pink
						break;
					case 10:
						actualColor = 0x999999; // Silver
						break;
				}
				
				// Set the player's name color
				player.getAppearance().setNameColor(actualColor);
				
				// Broadcast the updated user info
				player.broadcastUserInfo();
				
				// Notify the player
				player.sendMessageS("Your name color has been changed to " + selectedColor + ".", 8);
			}
			else
			{
				player.sendMessage("Invalid color selection: " + selectedColor);
			}
		}





// vmilon add clan reputation
		else if (command.startsWith("addclanrep"))
		{
			int id = CommunityDonateConfigs.CLAN_REP_PRICE_ID;
			int price = CommunityDonateConfigs.CLAN_REP_PRICE;
			int reputation = player.getClan().getReputationScore();
			if (Conditions.checkPlayerItemCount(player, id, price))
			{
				if ((reputation + 10000) > 100000000)
				{
					player.sendMessage("Your clan cannot have more than 100.000.000 Reputation Points");
					return;
				}
				
				player.destroyItemByItemId("addclanrep", id, price, player, true);
				player.getClan().addReputationScore(CommunityDonateConfigs.CLAN_REP_AMOUNT, true);
				player.sendMessageS("You got" + CommunityDonateConfigs.CLAN_REP_AMOUNT + "clan reputation. Your clan's reputation score is now " + player.getClan().getReputationScore() + "!", 8);
				logDonationAction(player, "Clan Reputation");
			}
			else
			{
				player.sendMessage("You need " + price + " Coins of Eternia to perform this action!");
			}
		}
