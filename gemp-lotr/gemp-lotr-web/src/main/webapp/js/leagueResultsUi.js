var LeagueResultsUI = Class.extend({
    communication: null,

    init: function(url) {
        this.communication = new GempLotrCommunication(url,
                function(xhr, ajaxOptions, thrownError) {
                });
        this.loadResults();
    },

    loadResults: function() {
        var that = this;
        this.communication.getLeagues(
                function(xml) {
                    that.loadedLeagueResults(xml);
                });
    },

    getDateString: function(date) {
        return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
    },

    loadedLeagueResults: function(xml) {
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'leagues') {
            $("#leagueResults").html("");

            var leagues = root.getElementsByTagName("league");
            for (var i = 0; i < leagues.length; i++) {
                var league = leagues[i];
                var leagueName = league.getAttribute("name");
                var cost = parseInt(league.getAttribute("cost"));
                var start = league.getAttribute("start");
                var end = league.getAttribute("end");

                var leagueText = leagueName;
                $("#leagueResults").append("<h1 class='leagueName'>" + leagueText + "</h1>");

                var costStr = Math.floor(cost / 100) + "G " + cost % 100 + "S";
                $("#leagueResults").append("<div class='leagueCost'>Cost: " + costStr + "</div>");

                var duration = this.getDateString(start) + " to " + this.getDateString(end);
                $("#leagueResults").append("<div class='leagueDuration'>Duration (GMT+0): " + duration + "</div>");

                var tabDiv = $("<div width='100%'></div>");
                var tabNavigation = $("<ul></ul>");
                tabDiv.append(tabNavigation);

                // Overall tab
                var tabContent = $("<div id='league" + i + "overall'></div>");

                var standings = league.getElementsByTagName("leagueStanding");
                if (standings.length > 0)
                    tabContent.append(this.createStandingsTable(standings));
                tabDiv.append(tabContent);

                tabNavigation.append("<li><a href='#league" + i + "overall'>Overall results</a></li>");

                var series = league.getElementsByTagName("serie");
                for (var j = 0; j < series.length; j++) {

                    var tabContent = $("<div id='league" + i + "serie" + j + "'></div>");

                    var serie = series[j];
                    var serieName = serie.getAttribute("type");
                    var serieStart = serie.getAttribute("start");
                    var serieEnd = serie.getAttribute("end");
                    var maxMatches = serie.getAttribute("maxMatches");
                    var format = serie.getAttribute("format");
                    var collection = serie.getAttribute("collection");
                    var limited = serie.getAttribute("limited");

                    var serieText = serieName + " - " + this.getDateString(serieStart) + " to " + this.getDateString(serieEnd);
                    tabContent.append("<h2 class='serieName'>" + serieText + "</h2>");

                    tabContent.append("<div><b>Format:</b> " + ((limited == "true") ? "Limited" : "Constructed") + " " + format + "</div>");
                    tabContent.append("<div><b>Collection:</b> " + collection + "</div>");

                    tabContent.append("<div>Maximum ranked matches in serie: " + maxMatches + "</div>");

                    var standings = serie.getElementsByTagName("standing");
                    if (standings.length > 0)
                        tabContent.append(this.createStandingsTable(standings));
                    tabDiv.append(tabContent);

                    tabNavigation.append("<li><a href='#league" + i + "serie" + j + "'>Serie " + (j + 1) + "</a></li>");
                }

                tabDiv.tabs();

                $("#leagueResults").append(tabDiv);
            }
        }
    },

    createStandingsTable: function(standings) {
        var standingsTable = $("<table class='standings'></table>");

        standingsTable.append("<tr><th>Standing</th><th>Player</th><th>Points</th><th>Games played</th><th>Opp. Win %</th><th></th><th>Standing</th><th>Player</th><th>Points</th><th>Games played</th><th>Opp. Win %</th></tr>");

        var secondColumnBaseIndex = Math.ceil(standings.length / 2);

        for (var k = 0; k < secondColumnBaseIndex; k++) {
            var standing = standings[k];
            var currentStanding = standing.getAttribute("standing");
            var player = standing.getAttribute("player");
            var points = parseInt(standing.getAttribute("points"));
            var gamesPlayed = parseInt(standing.getAttribute("gamesPlayed"));
            var opponentWinPerc = standing.getAttribute("opponentWin");

            standingsTable.append("<tr><td>" + currentStanding + "</td><td>" + player + "</td><td>" + points + "</td><td>" + gamesPlayed + "</td><td>" + opponentWinPerc + "</td></tr>");
        }

        for (var k = secondColumnBaseIndex; k < standings.length; k++) {
            var standing = standings[k];
            var currentStanding = standing.getAttribute("standing");
            var player = standing.getAttribute("player");
            var points = parseInt(standing.getAttribute("points"));
            var gamesPlayed = parseInt(standing.getAttribute("gamesPlayed"));
            var opponentWinPerc = standing.getAttribute("opponentWin");

            $("tr:eq(" + (k - secondColumnBaseIndex + 1) + ")", standingsTable).append("<td></td><td>" + currentStanding + "</td><td>" + player + "</td><td>" + points + "</td><td>" + gamesPlayed + "</td><td>" + opponentWinPerc + "</td>");
        }

        return standingsTable;
    }
});