package com.mygdx.game.net.messages.common;
//do not send sender
public class MoveItemFromStorageToStorage {
    public long uid;
    public int sourceX = Integer.MIN_VALUE, sourceY = Integer.MIN_VALUE, targetX = Integer.MIN_VALUE, targetY = Integer.MIN_VALUE;
    public long sourceId = Long.MIN_VALUE, targetId = Long.MIN_VALUE;
    public byte type = -1; // 0 - from sourceXY to targetXY, 1 - from sourceId to targetXY, 2 - from sourceXY to targetId, 3 - from sourceId to targetId

    public MoveItemFromStorageToStorage set(long id, int sourceX, int sourceY, int targetX, int targetY) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.targetX = targetX;
        this.targetY = targetY;
        uid = id;
        this.type = 0;
        return this;
    }

    public MoveItemFromStorageToStorage set(long id, long sourceId, int targetX, int targetY) {
        this.sourceId = sourceId;
        this.targetX = targetX;
        this.targetY = targetY;
        this.type = 1;
        uid = id;
        return this;
    }

    public MoveItemFromStorageToStorage set(long id, int sourceX, int sourceY, long targetId) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.targetId = targetId;
        this.type = 2;
        uid = id;
        return this;
    }

    public MoveItemFromStorageToStorage set(long id, long sourceId, long targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.type = 3;
        uid = id;
        return this;
    }
    
}
