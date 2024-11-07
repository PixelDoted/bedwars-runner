package me.pixeldots.Game.data;

public class PlayerStatistics {

    public int kills = 0;
    public int finalKills = 0;
    public int bedsBroken = 0;

    public int team = 0;
    public PlayerDead isDead = null;
    public String lastTitle = "";
    public long isInPVP = -1;
    public long magicMilkTime = -1;

    public int trackingTeam = -1;

    public PlayerStatistics() {}
    public PlayerStatistics(int _team) {
        this.team = _team;
    }

    public static class PlayerDead {
        public long timeDied = 0;
        public boolean hasBed = true;
    
        public PlayerDead(long _timeDied, boolean _hasBed) {
            this.timeDied = _timeDied;
            this.hasBed = _hasBed;
        }
    }
    

}
