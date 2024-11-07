package me.pixeldots.Game.data;

import org.bukkit.util.Vector;

public class DirectionVector {

    public Vector pos;
    public float pitch, yaw;

    public DirectionVector(Vector _pos, float _pitch, float _yaw) {
        this.pos = _pos;
        this.pitch = _pitch;
        this.yaw = _yaw;
    }
    public DirectionVector(float _x, float _y, float _z, float _pitch, float _yaw) {
        this(new Vector(_x, _y, _z), _pitch, _yaw);
    }
    
}
