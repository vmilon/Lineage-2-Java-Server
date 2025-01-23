This is a project I worked on during my employment at Biznest. This Java code was written in Eclipse IDE.

It is a Java emulator which I edited and added features to.

Due to the nature and size of the project, I cannot share every class and method that i used to create my features.

FEATURES I ADDED:




-L2LoyaltyInstance.java
An Instance of NPC that acts as a "loyalty" shop, the player receives loyalty points every 10 minutes he is online and he can spend them on this shop.

-L2PromoInstance.java
An Instance of NPC that receives input from the player, if there is an active promo code in our database the user receives gifts.


-L2DonateManagerInstance.java
An Instance of NPC that allows the user to use many services in exchange for donation coins. This file was created by another developer. I simply included a function that logged in a CSV file the actions the players used so we can keep track of what the players wanted mostly and to help us with any bug reports.


-isSpamming()
A function that simply issued a temporary chat block to a player that spammed on global chats.
