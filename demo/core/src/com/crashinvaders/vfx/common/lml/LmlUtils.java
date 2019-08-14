package com.crashinvaders.vfx.common.lml;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlView;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.parser.action.ActionContainerWrapper;
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

    public static <T extends Actor> T parseLmlTemplate(LmlParser lmlParser, LmlView viewController, FileHandle fileHandle) {
        // Check if the view controller was added as an action container already.
        final ActionContainerWrapper acw = lmlParser.getData().getActionContainer(viewController.getViewId());
        final ActionContainer actionContainer;
        if (acw != null) {
            actionContainer = acw.getActionContainer();
        } else {
            actionContainer = null;
        }

        Array<Actor> actors = lmlParser.createView(viewController, fileHandle);

        if (actionContainer != null) {
            // LmlParser removes action container after layout parsing. Let's add it back.
            lmlParser.getData().addActionContainer(viewController.getViewId(), actionContainer);
        }

        if (viewController.getStage() != null) {
            // LmlParser adds created actors directly to the stage after layout parsing.
            // Now we should remove them manually...
            for (Actor actor : actors) {
                actor.remove();
            }
        }

        return (T) actors.first();
    }

    public static <T extends Actor> T parseLmlTemplate(LmlParser lmlParser, Object view, FileHandle fileHandle) {
        return (T)lmlParser.createView(view, fileHandle).first();
    }

    public static <T extends Actor> T parseLmlTemplate(LmlParser lmlParser, FileHandle fileHandle) {
        return (T)lmlParser.parseTemplate(fileHandle).first();
    }
}
