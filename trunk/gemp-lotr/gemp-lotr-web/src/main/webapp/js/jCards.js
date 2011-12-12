var cardCache = {};
var cardScale = 357 / 497;

var Card = Class.extend({
    blueprintId: null,
    foil: null,
    tengwar: null,
    horizontal: null,
    imageUrl: null,
    zone: null,
    cardId: null,
    owner: null,
    siteNumber: null,
    attachedCards: null,

    init: function(blueprintId, zone, cardId, owner, siteNumber) {
        this.blueprintId = blueprintId;

        var len = blueprintId.length;
        this.foil = blueprintId.substring(len - 1, len) == "*";
        if (this.foil)
            blueprintId = blueprintId.substring(0, len - 1);
        len = blueprintId.length;
        this.tengwar = blueprintId.substring(len - 1, len) == "T";
        if (this.tengwar)
            blueprintId = blueprintId.substring(0, len - 1);

        this.zone = zone;
        this.cardId = cardId;
        this.owner = owner;
        if (siteNumber)
            this.siteNumber = parseInt(siteNumber);
        this.attachedCards = new Array();
        if (blueprintId == "rules") {
            this.imageUrl = "/gemp-lotr/images/rules.png";
        } else {
            if (cardCache[this.blueprintId] != null) {
                var cardFromCache = cardCache[this.blueprintId];
                this.horizontal = cardFromCache.horizontal;
                this.imageUrl = cardFromCache.imageUrl;
            } else {
                this.imageUrl = this.getUrlByBlueprintId(this.blueprintId);
                this.horizontal = this.isHorizontal(this.blueprintId);
                cardCache[this.blueprintId] = {
                    imageUrl: this.imageUrl,
                    horizontal: this.horizontal
                };
            }
        }
    },

    isTengwar: function() {
        return this.tengwar;
    },

    isFoil: function() {
        return this.foil;
    },

    isHorizontal: function(blueprintId) {
        var separator = blueprintId.indexOf("_");
        var setNo = parseInt(blueprintId.substr(0, separator));
        var cardNo = parseInt(blueprintId.substr(separator + 1));

        if (setNo == 1)
            return (cardNo >= 319 && cardNo <= 363);
        if (setNo == 2)
            return (cardNo >= 115 && cardNo <= 120);
        if (setNo == 3)
            return (cardNo >= 115 && cardNo <= 120);
        if (setNo == 4)
            return (cardNo >= 323 && cardNo <= 363);
        if (setNo == 5)
            return (cardNo >= 118 && cardNo <= 120);
        if (setNo == 6)
            return (cardNo >= 115 && cardNo <= 120);
        if (setNo == 7)
            return (cardNo >= 329 && cardNo <= 363);
        if (setNo == 8)
            return (cardNo >= 117 && cardNo <= 120);
        if (setNo == 10)
            return (cardNo >= 117 && cardNo <= 120);
        if (setNo == 11)
            return (cardNo >= 227 && cardNo <= 266);
        return false;
    },

    getUrlByBlueprintId: function(blueprintId) {
        if (blueprintId == "FotR - League Starter")
            return "/gemp-lotr/images/boosters/fotr_league_starter.png";
        else if (blueprintId == "FotR - Gandalf Starter")
            return "/gemp-lotr/images/boosters/fotr_gandalf_starter.png";
        else if (blueprintId == "FotR - Aragorn Starter")
                return "/gemp-lotr/images/boosters/fotr_aragorn_starter.png";
            else if (blueprintId == "FotR - Booster")
                    return "/gemp-lotr/images/boosters/fotr_booster.png";

        var separator = blueprintId.indexOf("_");
        var setNo = parseInt(blueprintId.substr(0, separator));
        var cardNo = parseInt(blueprintId.substr(separator + 1));

        var setNoStr;
        if (setNo < 10)
            setNoStr = "0" + setNo;
        else
            setNoStr = setNo;

        if (cardNo < 10)
            return "http://lotrtcgdb.com/images/LOTR" + setNoStr + "00" + cardNo + (this.isTengwar() ? "T" : "") + ".jpg";
        else if (cardNo < 100)
            return "http://lotrtcgdb.com/images/LOTR" + setNoStr + "0" + cardNo + (this.isTengwar() ? "T" : "") + ".jpg";
        else
            return "http://lotrtcgdb.com/images/LOTR" + setNoStr + "" + cardNo + (this.isTengwar() ? "T" : "") + ".jpg";
    },

    getHeightForWidth: function(width) {
        if (this.horizontal)
            return Math.floor(width * cardScale);
        else
            return Math.floor(width / cardScale);
    },

    getWidthForHeight: function(height) {
        if (this.horizontal)
            return Math.floor(height / cardScale);
        else
            return Math.floor(height * cardScale);
    },

    getWidthForMaxDimension: function(maxDimension) {
        if (this.horizontal)
            return maxDimension;
        else
            return Math.floor(maxDimension * cardScale);
    }
});

function createCardDiv(image, text, foil, tokens) {
    var cardDiv = $("<div class='card'><img src='" + image + "' width='100%' height='100%'>" + ((text != null) ? text : "") + "</div>");
    if (foil) {
        var foilDiv = $("<div class='foilOverlay'><img src='/gemp-lotr/images/foil.gif' width='100%' height='100%'></div>");
        cardDiv.append(foilDiv);
    }

    if (tokens === undefined || tokens) {
        var overlayDiv = $("<div class='tokenOverlay'></div>");
        cardDiv.append(overlayDiv);
    }
    var borderDiv = $("<div class='borderOverlay'><img class='actionArea' src='/gemp-lotr/images/pixel.png' width='100%' height='100%'></div>");
    cardDiv.append(borderDiv);

    return cardDiv;
}

function createSimpleCardDiv(image) {
    var cardDiv = $("<div class='card'><img src='" + image + "' width='100%' height='100%'></div>");

    return cardDiv;
}
