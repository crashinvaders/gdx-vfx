/*******************************************************************************
 * Copyright 2019 metaphore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.crashinvaders.vfx.common.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import static com.badlogic.gdx.utils.Align.*;

public class Scene2dUtils {
    private static final String TAG_INJECT_FIELDS = "InjectActorFields";
    private static final Vector2 tmpVec2 = new Vector2();
    private static final Rectangle tmpRect = new Rectangle();

    public static void simulateClick(Actor actor) {
        simulateClick(actor, 0, 0, 0f, 0f);
    }

    public static void simulateClick(Actor actor, int button, int pointer, float localX, float localY) {
        Vector2 pos = actor.stageToLocalCoordinates(tmpVec2.set(localX, localY));
        simulateClickGlobal(actor, button, pointer, pos.x, pos.y);
    }

    public static void simulateClickGlobal(Actor actor, int button, int pointer, float stageX, float stageY) {
        InputEvent event = Pools.obtain(InputEvent.class);
        event.setStage(actor.getStage());
        event.setRelatedActor(actor);
        event.setTarget(actor);
        event.setStageX(stageX);
        event.setStageY(stageY);
        event.setButton(button);
        event.setPointer(pointer);

        event.setType(InputEvent.Type.touchDown);
        actor.notify(event, false);
        event.setType(InputEvent.Type.touchUp);
        actor.notify(event, false);

        Pools.free(event);
    }

    /** Injects actors from group into target's fields annotated with {@link InjectActor} using reflection. */
    public static void injectActorFields(Object target, Group group) {
        Class<?> handledClass = target.getClass();
        while (handledClass != null && !handledClass.equals(Object.class)) {
            for (final Field field : ClassReflection.getDeclaredFields(handledClass)) {
                if (field != null && field.isAnnotationPresent(InjectActor.class)) {
                    try {
                        InjectActor annotation = field.getDeclaredAnnotation(InjectActor.class).getAnnotation(InjectActor.class);
                        String actorName = annotation.value();
                        if (actorName.length() == 0) {
                            actorName = field.getName();
                        }
                        Actor actor = group.findActor(actorName);
                        if (actor == null && actorName.equals(group.getName())) {
                            actor = group;
                        }
                        if (actor == null) {
                            Gdx.app.error(TAG_INJECT_FIELDS, "Can't find actor with name: " + actorName + " in group: " + group + " to inject into: " + target);
                        } else {
                            field.setAccessible(true);
                            field.set(target, actor);
                        }
                    } catch (final ReflectionException exception) {
                        Gdx.app.error(TAG_INJECT_FIELDS, "Unable to set value into field: " + field + " of object: " + target, exception);
                    }
                }
            }
            handledClass = handledClass.getSuperclass();
        }
    }

    public static void setColorRecursively(Actor actor, Color color) {
        if (actor instanceof Group) {
            Group group = (Group) actor;
            for (Actor child : group.getChildren()) {
                setColorRecursively(child, color);
            }
        }
        actor.setColor(color);
    }

    public static Vector2 setPositionRelative(Actor srcActor, int srcAlign, Actor dstActor, int dstAlign, float dstX, float dstY, boolean round) {
        Vector2 pos = tmpVec2.set(srcActor.getX(srcAlign), srcActor.getY(srcAlign));

        if ((dstAlign & right) != 0)
            pos.x -= dstActor.getWidth();
        else if ((dstAlign & left) == 0)
            pos.x -= dstActor.getWidth() / 2;

        if ((dstAlign & top) != 0)
            pos.y -= dstActor.getHeight();
        else if ((dstAlign & bottom) == 0)
            pos.y -= dstActor.getHeight() / 2;

        pos.add(dstX, dstY);

        if (round) {
            pos.set(pos.x, pos.y);
        }
        dstActor.setPosition(pos.x, pos.y);
        return pos;
    }

    /** Finds top most layout the parent hierarchy. */
    public static Layout findTopMostLayout(Actor actor) {
        Layout topMostLayout = null;
        if (actor instanceof Layout) {
            topMostLayout = (Layout) actor;
        }

        Group parent = actor.getParent();
        if (parent instanceof Layout) {
            Layout parentTopMostLayout = findTopMostLayout(parent);
            if (parentTopMostLayout != null) {
                topMostLayout = parentTopMostLayout;
            }
        }
        return topMostLayout;
    }

    public static Rectangle localToStageBounds(Actor actor) {
        return localToStageBounds(actor, 0f, 0f, actor.getWidth(), actor.getHeight());
    }

    public static Rectangle localToStageBounds(Actor actor, Rectangle localBounds) {
        return localToStageBounds(actor,
                localBounds.x,
                localBounds.y,
                localBounds.x + localBounds.width,
                localBounds.y + localBounds.height);
    }

    public static Rectangle localToStageBounds(Actor actor, float x0, float y0, float x1, float y1) {
        Rectangle stageRect = Scene2dUtils.tmpRect;
        actor.localToStageCoordinates(tmpVec2.set(x0, y0));
        stageRect.setX(tmpVec2.x);
        stageRect.setY(tmpVec2.y);
        actor.localToStageCoordinates(tmpVec2.set(x1, y1));
        stageRect.setWidth(tmpVec2.x - stageRect.x);
        stageRect.setHeight(tmpVec2.y - stageRect.y);
        return stageRect;
    }

    public static Rectangle stageToLocalBounds(Actor actor, Rectangle stageBounds) {
        return localToStageBounds(actor,
                stageBounds.x,
                stageBounds.y,
                stageBounds.x + stageBounds.width,
                stageBounds.y + stageBounds.height);
    }

    public static Rectangle stageToLocalBounds(Actor actor, float x0, float y0, float x1, float y1) {
        Rectangle localRect = Scene2dUtils.tmpRect;
        actor.stageToLocalCoordinates(tmpVec2.set(x0, y0));
        localRect.setX(tmpVec2.x);
        localRect.setY(tmpVec2.y);
        actor.stageToLocalCoordinates(tmpVec2.set(x1, y1));
        localRect.setWidth(tmpVec2.x - localRect.x);
        localRect.setHeight(tmpVec2.y - localRect.y);
        return localRect;
    }
}
