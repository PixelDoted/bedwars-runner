package me.pixeldots.Game.data;

public class TeamObjectData<T> {

    public int team;
    public T data;
    
    public TeamObjectData() {}
    public TeamObjectData(int _team, T _data) {
        this.team = _team;
        this.data = _data;
    }

}
