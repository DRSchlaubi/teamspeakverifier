(This Plugin is made with the TeamSpeakAPI of https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API)
**What does this plugin do?**
With this plugin you can link your Minecraft and Teamspeak accounts and roles/ranks

**Requirements**
* - MySQL database
* - TeamSpeak query access
* - Vault and an suppoerted permission plugin (Spigot only)

**Commands**
* /teamspeak verify - Verify command 
* /teamspeak unlink - "Unverify" command
* /teamspeak update - Updates roles when you asigned a new role to the user
* /teamspeak reload - reload command Permission: ts.reload

**Installation**
* Download the newest version of the plugin and place it in your plugins/ folder
* Restart your server
* Modify config.yml
* Restart your server again
* DONE
**Developer API**
This plugin has an integrated API when you you want to use it just add the plugin to your project and the following line to your plugin/bungee.yml
`depend: [TeamspeakVerifyer]`

Methods:
`//Tests of Teamspeak DBID is verified`
`TeamspeakVerifyerAPI.isVerified(String identity)`
`//Tests of Player is verified`
`TeamspeakVerifyerAPI.isVerified(Player player)`
`//Tests of ProxiedPlayer is verified`
`TeamspeakVerifyerAPI.isVerified(ProxiedPlayer player)`
`//Retrives MCUsername from TeamSpeak DBID`
`TeamspeakVerifyerAPI.getUserName(String id)`
`//Retrives TeamSpeak DBID from Player`
`TeamspeakVerifyerAPI.getDatabaseId(Player player)`
`//Retrives TeamSpeak DBID from Player`
`TeamspeakVerifyerAPI.getDatabaseId.getDatabaseId(ProxiedPlayer player)`
