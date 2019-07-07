package com.crashinvaders.vfx.common.lml;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlView;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.parser.impl.tag.Dtd;

import java.io.Writer;

public class LmlUtils {
    public static void saveDtdSchema(final LmlParser lmlParser, final FileHandle file) {
        try {
            final Writer appendable = file.writer(false, "UTF-8");
            final boolean strict = lmlParser.isStrict();
            lmlParser.setStrict(false); // Temporary setting to non-strict to generate as much tags as possible.
            Dtd.saveSchema(lmlParser, appendable);
            appendable.close();
            lmlParser.setStrict(strict);
        } catch (final Exception exception) {
            throw new GdxRuntimeException("Unable to save DTD schema.", exception);
        }
    }

    public static <T extends Actor> T parseLmlTemplate(LmlParser lmlParser, Object viewController, boolean registerActions, FileHandle fileHandle) {
        ActionContainer actionContainer = null;
        LmlView lmlView = null;
        if (registerActions) {
            if (viewController instanceof ActionContainer) actionContainer = (ActionContainer) viewController;
            if (viewController instanceof LmlView) lmlView = (LmlView) viewController;
        }

        if (actionContainer != null && lmlView != null) {
            lmlParser.getData().addActionContainer(lmlView.getViewId(), actionContainer);
        }
        Array<Actor> actors = lmlParser.createView(viewController, fileHandle);
        if (actionContainer != null && lmlView != null) {
            lmlParser.getData().removeActionContainer(lmlView.getViewId());
        }

        // LmlParser will add created actors directly to the stage after creation.
        // Now we should remove them...
        for (Actor actor : actors) {
            actor.remove();
        }

        return (T) actors.first();
    }
}
