<strong>This Java code was written in Eclipse IDE.</strong>

It is a Java emulator for Lineage 2 which I edited and added features to.

Due to the nature and size of the project, I cannot share every class and method that i used to create my features nor every feature i added. I will try and include the most noteworthy efforts.

<strong>FEATURES I ADDED</strong>:



<strong>-L2LoyaltyInstance.java</strong>
An Instance of NPC that acts as a "loyalty" shop, the player receives loyalty points every 10 minutes he is online and he can spend them on this shop.


<strong>-L2PromoInstance.java</strong>
An Instance of NPC that receives input from the player, if there is an active promo code in our database the user receives gifts.


<strong>-L2DonateManagerInstance.java</strong>
An Instance of NPC that allows the user to use many services in exchange for donation coins. This file was created by another developer. I simply included a function that logged in a CSV file the actions the players used so we can keep track of what the players wanted mostly and to help us with any bug reports.


<strong>-isSpamming()</strong>
A function that simply issued a temporary chat block to a player that spammed on global chats.


<strong>-PremiumHandler.java</strong>
Logic for premium account. This system was already in place by another developer. I split the premium in 2 parts (Gold and Platinum) and added the option to extend the remaining time of the players premium account.
Previously you had to wait for the premium account to expire.


<strong>-AioSigns.java</strong>
A merging of 3 NPCS (blacksmith of mammon, merchant of mammon, black marketeer of mammon). This was created as a 'Quality of Life' feature. Most of the features of the NPC are simply copied from the original NPCs, I simply merged and adapted them.



<strong>Disclaimer</strong>: I am in no way an expert in Java. This was a project that i was assigned to by my employer. I am simply showcasing some of my work. Thanks for taking the time to read this
