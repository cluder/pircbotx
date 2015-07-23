/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks;

import com.google.common.base.Supplier;
import com.google.common.collect.ComparisonChain;
import java.util.concurrent.atomic.AtomicLong;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.types.GenericEvent;

/**
 * An event representing what was received from the IRC server.
 *
 * @author Leon Blakey
 */
@Getter
@EqualsAndHashCode
public abstract class Event implements GenericEvent {
	/**
	 * Value is 0-based number of events
	 */
	protected static final AtomicLong EVENT_COUNTER = new AtomicLong();
	/**
	 * Returns the millisecond when the event was created.
	 *
	 * @return A timestamp as a long
	 */
	protected final long timestamp;
	/**
	 * Returns the {@link PircBotX} instance that this event originally came
	 * from.
	 *
	 * @return A {@link PircBotX} instance
	 */
	protected final PircBotX bot;
	/**
	 * Returns the id of this event. This id is guaranteed to be unique in the
	 * context of other events dispatched from the same listener manager.
	 *
	 * @return The id of this event
	 */
	protected final long id;

	public Event(PircBotX bot) {
		this.timestamp = System.currentTimeMillis();
		this.bot = bot;
		this.id = EVENT_COUNTER.getAndIncrement();
	}

	/**
	 * A simple abstract method that all events must implement to respond to an
	 * event happening. All implementing classes should delegate to the
	 * sendMessage or other relevant methods in the main PircBotX class, not
	 * with custom lines and calls to the send() methods
	 *
	 * @param response The response to send
	 */
	public abstract void respond(String response);

	/**
	 * Compare events by {@link #getTimestamp()} and then {@link #getId()} to
	 * order by when they are received. This is useful for sorting lists of
	 * Channel objects.
	 *
	 * @param other Other Event to compare to
	 * @return the result of the comparison
	 */
	public int compareTo(Event other) {
		ComparisonChain comparison = ComparisonChain.start()
				.compare(getTimestamp(), other.getTimestamp())
				.compare(getId(), other.getId());
		if (bot != null && other.getBot() != null)
			comparison.compare(bot.getBotId(), other.getBot().getBotId());
		return comparison.result();
	}

	@SuppressWarnings("unchecked")
	public <T extends PircBotX> T getBot() {
		return (T) bot;
	}
}
