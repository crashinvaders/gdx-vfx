package com.crashinvaders.vfx.common.lml;

import com.github.czyzby.lml.parser.action.ActorConsumer;

public class EmptyActorConsumer implements ActorConsumer<Void, Object> {
    @Override
    public Void consume(Object actor) {
        return null;
    }
}
