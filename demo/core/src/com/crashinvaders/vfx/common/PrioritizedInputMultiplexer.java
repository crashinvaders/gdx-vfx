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

package com.crashinvaders.vfx.common;

import com.badlogic.gdx.InputProcessor;
import com.crashinvaders.vfx.utils.CommonUtils;
import com.crashinvaders.vfx.utils.ValueArrayMap;

import java.util.Comparator;

/**
 * Same old InputMultiplexer, but orders processors by an assigned priority.
 * The higher priority, the earlier processor will be called.
 */
public class PrioritizedInputMultiplexer implements InputProcessor {
    private final Comparator<Wrapper> comparator;
    private ValueArrayMap<InputProcessor, Wrapper> processors = new ValueArrayMap<>(4);

    private int maxPointers = Integer.MAX_VALUE; // Multitouch by default

    public PrioritizedInputMultiplexer() {
        comparator = new WrapperComparator();
    }

    public int getMaxPointers() {
        return maxPointers;
    }

    public void setMaxPointers(int maxPointers) {
        this.maxPointers = maxPointers;
    }

    public void addProcessor (InputProcessor processor) {
        addProcessor(processor, 0);
    }

    public void addProcessor (InputProcessor processor, int priority) {
		if (processor == null) throw new NullPointerException("processor cannot be null");
		processors.put(processor, new Wrapper(processor, priority));
        processors.sort(comparator);
	}

	public void removeProcessor (InputProcessor processor) {
        processors.remove(processor);
	}

	/** @return the number of processors in this multiplexer */
	public int size () {
		return processors.size();
	}

	public void clear () {
		processors.clear();
	}

	@Override
    public boolean keyDown (int keycode) {
		for (int i = 0, n = processors.size(); i < n; i++)
			if (processors.getValueAt(i).keyDown(keycode)) return true;
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		for (int i = 0, n = processors.size(); i < n; i++)
			if (processors.getValueAt(i).keyUp(keycode)) return true;
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		for (int i = 0, n = processors.size(); i < n; i++)
			if (processors.getValueAt(i).keyTyped(character)) return true;
		return false;
	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (pointer >= maxPointers) return false;

		for (int i = 0, n = processors.size(); i < n; i++)
			if (processors.getValueAt(i).touchDown(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (pointer >= maxPointers) return false;

		for (int i = 0, n = processors.size(); i < n; i++)
			if (processors.getValueAt(i).touchUp(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (pointer >= maxPointers) return false;

		for (int i = 0, n = processors.size(); i < n; i++)
			if (processors.getValueAt(i).touchDragged(screenX, screenY, pointer)) return true;
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		for (int i = 0, n = processors.size(); i < n; i++)
			if (processors.getValueAt(i).mouseMoved(screenX, screenY)) return true;
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		for (int i = 0, n = processors.size(); i < n; i++)
			if (processors.getValueAt(i).scrolled(amount)) return true;
		return false;
	}

    private static class Wrapper implements InputProcessor {
        private final InputProcessor processor;
        private final int priority;

        public Wrapper(InputProcessor processor, int priority) {
            this.processor = processor;
            this.priority = priority;
        }
        @Override
        public boolean keyDown(int keycode) {
            return processor.keyDown(keycode);
        }
        @Override
        public boolean keyUp(int keycode) {
            return processor.keyUp(keycode);
        }
        @Override
        public boolean keyTyped(char character) {
            return processor.keyTyped(character);
        }
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return processor.touchDown(screenX, screenY, pointer, button);
        }
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return processor.touchUp(screenX, screenY, pointer, button);
        }
        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return processor.touchDragged(screenX, screenY, pointer);
        }
        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return processor.mouseMoved(screenX, screenY);
        }
        @Override
        public boolean scrolled(int amount) {
            return processor.scrolled(amount);
        }

        @Override
        public String toString() {
            if (processor != null) {
                return processor.toString();
            }
            return super.toString();
        }
    }

    private static class WrapperComparator implements Comparator<Wrapper> {
        @Override
        public int compare(Wrapper l, Wrapper r) {
            return CommonUtils.compare(r.priority, l.priority);
        }
    }
}
