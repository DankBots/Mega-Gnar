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
    private static void freeToPlay(Context context, Platform platform) {
        try {
            List champions = Bot.getRiotAPI().getChampions(platform, true).getChampions();
            StringBuilder championList = new StringBuilder();
            champions.forEach((c) -> {
                String championID = (String) c;
                String championName = RiotUtils.idToChampion(Integer.valueOf(championID)); //I should've just made it take a string in the first place but whatever
                championList.append(championID + ", ");
            });
            context.send().embed("Free to Play Champions").addField("Champions", championList.toString(), false)
                    .action().queue();
            return;

        } catch (RiotApiException e) {
            context.send().error(e.toString()).queue();
            return;
        }
    }


    private static void currentGame(Context context, Platform platform, String username) {
        try {
            Summoner summoner = Bot.getRiotAPI().getSummonerByName(platform, username);
            long summonerID = summoner.getId();
            String summonerName = summoner.getName();
            CurrentGameInfo gameInfo = Bot.getRiotAPI().getActiveGameBySummoner(platform, summonerID);
            StringBuilder bannedChampionList = new StringBuilder();
            gameInfo.getBannedChampions().forEach((c) -> bannedChampionList.append(RiotUtils.idToChampion(c.getChampionId()) + "\n"));
            StringBuilder participantsList = new StringBuilder();
            gameInfo.getParticipants().forEach((p) -> participantsList.append(p.getSummonerName() + "\n"));
            context.send().embed("Current Game Information for " + summonerName)
                    .field("Game Length", true, gameInfo.getGameLength() / 60)
                    .field("Banned Champions", true, bannedChampionList.toString())
                    .field("Players", true, participantsList.toString())
                    .field("Champion", true, RiotUtils.idToChampion(gameInfo.getParticipantByParticipantName(summonerName).getChampionId()))
                    .action().queue();
            return;
            //TODO: Add summoner spells and a few other things which require more database stuffs :D.
        } catch (RiotApiException e) {
            context.send().error("This player either doesn't exist or isn't currently in a game!").queue();
            return;
        }
    }


    private static void lastGame(Context context, Platform platform, String username)  {
        try {
            Summoner summoner = Bot.getRiotAPI().getSummonerByName(platform, username);
            long summonerID = summoner.getId();
            String summonerName = summoner.getName();
            long matchID = Bot.getRiotAPI().getRecentMatchListByAccountId(platform, summonerID).getMatches().get(0).getGameId(); //This should get the latest Match.
            Match match = Bot.getRiotAPI().getMatch(platform, matchID);
            Participant ourSummoner = match.getParticipantBySummonerId(summonerID);
            context.send().embed("Last Game Info for " + summonerName)
                    .field("Champion", true, RiotUtils.idToChampion(ourSummoner.getChampionId()))
                    .field("KDA", true, ourSummoner.getStats().getKills() + "/" + ourSummoner.getStats().getAssists() + "/" + ourSummoner.getStats().getDeaths())
                    .field("Towers Destroyed", true, ourSummoner.getStats().getTurretKills())
                    .field("Champion Level", true, ourSummoner.getStats().getChampLevel())
                    .field("Killing Sprees", true, ourSummoner.getStats().getKillingSprees())
                    .field("Biggest Multi-Kill", true, ourSummoner.getStats().getLargestMultiKill())
                    .field("Total Damage Dealt", true, ourSummoner.getStats().getTotalDamageDealt())
                    .field("Total Damage Taken", true, ourSummoner.getStats().getTotalDamageTaken())
                    .action().queue();
            return;

            //TODO: So I've realized that I should just pull all the static data and put it in a database, and export it. I will add support for items once I have done this.
        } catch (RiotApiException e) {
            context.send().error("This player doesn't exist!").queue();
            return;
        }
    }


    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length == 0) {
            CommandDispatcher.INSTANCE.sendHelp(context, getInfo());
            return;
        }

        String region = args[0].toLowerCase();
        String action = args[1].toLowerCase();
        String username = args[2].toLowerCase();

        Platform platform = Platform.getPlatformByName(region);

                switch (action) {
                    case "freetoplay":
                        freeToPlay(context, platform);
                        return;
                    case "currentgame":
                        if (args.length < 2) {
                            CommandDispatcher.INSTANCE.sendHelp(context, getInfo());
                            return;
                        }
                        currentGame(context, platform, username);
                        return;
                    case "lastgame":
                        if (args.length < 2) {
                            CommandDispatcher.INSTANCE.sendHelp(context, getInfo());
                            return;
                        }
                        lastGame(context, platform, username);
                        return;

                    default:
                        context.send().error("The currently supported actions are: lastgame, currentgame, freetoplay").queue();
                        return;
        }
    }
}



