# RiksdagServiceProject

This is working as it should. Open for further dev. 

This Riksdag Api module uses core java 8 and org.JSON api 

/**
 * This Module can do 2 things.
 * 1. Get all data at once from riksdags api called Jumbo,
 * and write it to the disk as json file.
 * 2. Get data for a given name from jumbo.json file. 
 * This is called Bulk because names are taken from jumbo.json file one by one,
 * and all json files for everyone are created in one go.
 * To run this initiate an Object with one of commands : JUMBO or BULK.
 * (or just run it without commands)
 * This is designed to do every one of the commands in a thread and pause;
 * the idea is to call JUMBO once a month for example, and then generate 
 * jsons for everyone. Those personal json files could be accessed later, often,
 * without overloading the server.
 * 
 * //TODO simple on off buttons GUI + while loop + timeintervall pause
 * 
 * @author lukas
 *
 */
