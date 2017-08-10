package xyz.gnarbot.gnar.commands.executors.games;


import net.rithms.riot.api.ApiMethod;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.MatchReference;
import net.rithms.riot.api.endpoints.match.dto.Participant;
import net.rithms.riot.api.endpoints.spectator.dto.CurrentGameInfo;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandDispatcher;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.ResponseBuilder;
import xyz.gnarbot.gnar.utils.RiotUtils;

import java.util.List;


@Command(
        id = 18,
        aliases = {"leagueoflegends", "league"},
        description = "League commands.",
        usage = "(region) (action) (username)"
)
public class LeagueCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length == 0) {
            CommandDispatcher.INSTANCE.sendHelp(context, getInfo());
            return;
        }

        String region  = args[0].toLowerCase();
        String action = args[1].toLowerCase();
        String username = args[2].toLowerCase();

        switch(region) {
            case "na":
                switch(action) {
                    case "freetoplay":
                        try {
                            List champions = Bot.getRiotAPI().getChampions(Platform.NA, true).getChampions();
                            StringBuilder championList = new StringBuilder();
                            champions.forEach((c) -> {
                               String championID = (String) c;
                               String championName = RiotUtils.idToChampion(Integer.valueOf(championID)); //I should've just made it take a string in the first place but whatever
                               championList.append(championID + ", ");
                            });
                            context.send().embed("Free to Play Champions").addField("Champions", championList.toString(), false); //This should hopefully work.
                            return;

                        } catch (RiotApiException e) {
                            context.send().error(e.toString());
                            return;
                        }
                    case "currentgame":
                        if (args.length < 2) {
                            CommandDispatcher.INSTANCE.sendHelp(context, getInfo());
                            return;
                        }
                        try {
                            Summoner summoner = Bot.getRiotAPI().getSummonerByName(Platform.NA, username);
                            long summonerID = summoner.getId();
                            String summonerName = summoner.getName();
                            CurrentGameInfo gameInfo = Bot.getRiotAPI().getActiveGameBySummoner(Platform.NA, summonerID);
                            StringBuilder bannedChampionList = new StringBuilder();
                            gameInfo.getBannedChampions().forEach((c) -> bannedChampionList.append(RiotUtils.idToChampion(c.getChampionId()) + "\n"));
                            StringBuilder participantsList = new StringBuilder();
                            gameInfo.getParticipants().forEach((p) -> participantsList.append(p.getSummonerName() + "\n"));
                            context.send().embed("Current Game Information for " + summonerName)
                                    .field("Game Length", true, gameInfo.getGameLength() / 60)
                                    .field("Banned Champions", true, bannedChampionList.toString())
                                    .field("Players", true, participantsList.toString())
                                    .field("Champion", true, RiotUtils.idToChampion(gameInfo.getParticipantByParticipantName(summonerName).getChampionId()));
                                    return;
                            //TODO: Add summoner spells and a few other things which require more database stuffs :D.
                        } catch (RiotApiException e) {
                            context.send().error("This player either doesn't exist or isn't currently in a game!");
                            return;
                        }
                    case "lastgame":
                        if (args.length < 2) {
                            CommandDispatcher.INSTANCE.sendHelp(context, getInfo());
                            return;
                        }

                        try {
                            Summoner summoner = Bot.getRiotAPI().getSummonerByName(Platform.NA, username);
                            long summonerID = summoner.getId();
                            String summonerName = summoner.getName();
                            long matchID = Bot.getRiotAPI().getRecentMatchListByAccountId(Platform.NA, summonerID).getMatches().get(0).getGameId(); //This should get the latest Match.
                            Match match = Bot.getRiotAPI().getMatch(Platform.NA, matchID);
                            Participant ourSummoner = match.getParticipantBySummonerId(summonerID);
                            context.send().embed("Last Game Info for " + summonerName)
                                    .field("Champion", true, RiotUtils.idToChampion(ourSummoner.getChampionId()))
                                    .field("KDA", true, ourSummoner.getStats().getKills() + "/" + ourSummoner.getStats().getAssists() + "/" + ourSummoner.getStats().getDeaths())
                                    .field("Towers Destroyed", true, ourSummoner.getStats().getTurretKills())
                                    .field("Champion Level", true, ourSummoner.getStats().getChampLevel())
                                    .field("Killing Sprees", true, ourSummoner.getStats().getKillingSprees())
                                    .field("Biggest Multi-Kill", true, ourSummoner.getStats().getLargestMultiKill())
                                    .field("Total Damage Dealt", true, ourSummoner.getStats().getTotalDamageDealt())
                                    .field("Total Damage Taken", true, ourSummoner.getStats().getTotalDamageTaken());
                            //I'm really tired, when i wake up i will expand this more :D
                            return;

                                    //TODO: So I've realized that I should just pull all the static data and put it in a database, and export it. I will add support for items once I have done this.
                        } catch (RiotApiException e) {
                            context.send().error("This player doesn't exist!");
                            return;
                        }

                    default:
                        context.send().error("The currently supported actions are: lastgame, currentgame, freetoplay");

                }

            case "eune":
                switch(action) {
                    case "freetoplay":
                        try {
                            List champions = Bot.getRiotAPI().getChampions(Platform.EUNE, true).getChampions();
                            StringBuilder championList = new StringBuilder();
                            champions.forEach((c) -> {
                                String championID = (String) c;
                                String championName = RiotUtils.idToChampion(Integer.valueOf(championID)); //I should've just made it take a string in the first place but whatever
                                championList.append(championID + ", ");
                            });
                            context.send().embed("Free to Play Champions").addField("Champions", championList.toString(), false); //This should hopefully work.
                            return;

                        } catch (RiotApiException e) {
                            context.send().error(e.toString());
                            return;
                        }
                    case "currentgame":
                        if (args.length < 2) {
                            CommandDispatcher.INSTANCE.sendHelp(context, getInfo());
                            return;
                        }
                        try {
                            Summoner summoner = Bot.getRiotAPI().getSummonerByName(Platform.EUNE, username);
                            long summonerID = summoner.getId();
                            String summonerName = summoner.getName();
                            CurrentGameInfo gameInfo = Bot.getRiotAPI().getActiveGameBySummoner(Platform.EUNE, summonerID);
                            StringBuilder bannedChampionList = new StringBuilder();
                            gameInfo.getBannedChampions().forEach((c) -> bannedChampionList.append(RiotUtils.idToChampion(c.getChampionId()) + "\n"));
                            StringBuilder participantsList = new StringBuilder();
                            gameInfo.getParticipants().forEach((p) -> participantsList.append(p.getSummonerName() + "\n"));
                            context.send().embed("Current Game Information for " + summonerName)
                                    .field("Game Length", true, gameInfo.getGameLength() / 60)
                                    .field("Banned Champions", true, bannedChampionList.toString())
                                    .field("Players", true, participantsList.toString())
                                    .field("Champion", true, RiotUtils.idToChampion(gameInfo.getParticipantByParticipantName(summonerName).getChampionId()));
                                    return;
                            //TODO: Add summoner spells and a few other things which require more database stuffs :D.
                        } catch (RiotApiException e) {
                            context.send().error("This player either doesn't exist or isn't currently in a game!");
                            return;
                        }
                    case "lastgame":
                        if (args.length < 2) {
                            CommandDispatcher.INSTANCE.sendHelp(context, getInfo());
                            return;
                        }

                        try {
                            Summoner summoner = Bot.getRiotAPI().getSummonerByName(Platform.EUNE, username);
                            long summonerID = summoner.getId();
                            String summonerName = summoner.getName();
                            long matchID = Bot.getRiotAPI().getRecentMatchListByAccountId(Platform.EUNE, summonerID).getMatches().get(0).getGameId(); //This should get the latest Match.
                            Match match = Bot.getRiotAPI().getMatch(Platform.EUNE, matchID);
                            Participant ourSummoner = match.getParticipantBySummonerId(summonerID);
                            context.send().embed("Last Game Info for " + summonerName)
                                    .field("Champion", true, RiotUtils.idToChampion(ourSummoner.getChampionId()))
                                    .field("KDA", true, ourSummoner.getStats().getKills() + "/" + ourSummoner.getStats().getAssists() + "/" + ourSummoner.getStats().getDeaths())
                                    .field("Towers Destroyed", true, ourSummoner.getStats().getTurretKills())
                                    .field("Champion Level", true, ourSummoner.getStats().getChampLevel())
                                    .field("Killing Sprees", true, ourSummoner.getStats().getKillingSprees())
                                    .field("Biggest Multi-Kill", true, ourSummoner.getStats().getLargestMultiKill())
                                    .field("Total Damage Dealt", true, ourSummoner.getStats().getTotalDamageDealt())
                                    .field("Total Damage Taken", true, ourSummoner.getStats().getTotalDamageTaken());
                                    return;
                            //I'm really tired, when i wake up i will expand this more :D


                            //TODO: So I've realized that I should just pull all the static data and put it in a database, and export it. I will add support for items once I have done this.
                        } catch (RiotApiException e) {
                            context.send().error("This player doesn't exist!");
                            return;
                        }

                    default:
                        context.send().error("The currently supported actions are: lastgame, currentgame, freetoplay");


                }



            default:
                context.send().error("The currently supported regions are: EUNE, NA");
                return;

        }



    }
}


