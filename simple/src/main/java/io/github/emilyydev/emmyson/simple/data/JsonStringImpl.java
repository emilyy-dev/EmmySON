//
// Simple implementation for the EmmySON API
// Copyright (C) 2022  emilyy-dev
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

package io.github.emilyydev.emmyson.simple.data;

import io.github.emilyydev.emmyson.data.JsonString;
import org.jetbrains.annotations.NotNull;

public final class JsonStringImpl implements JsonString {

  private static final long serialVersionUID = -8888634840703976187L;

  public static final JsonString EMPTY = new JsonStringImpl("");

  private final String string;

  public JsonStringImpl(final String string) {
    this.string = string;
  }

  @Override
  public int length() {
    return this.string.length();
  }

  @Override
  public char charAt(final int index) {
    return this.string.charAt(index);
  }

  @Override
  public @NotNull CharSequence subSequence(final int start, final int end) {
    return this.string.subSequence(start, end);
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof JsonString)) { return false; }
    return this.string.equals(((JsonString) other).toString());
  }

  @Override
  public int hashCode() {
    return this.string.hashCode();
  }

  @Override
  public String toString() {
    return this.string;
  }
}
