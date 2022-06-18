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

package io.github.emilyydev.emmyson.simple.io;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

// A Reader for Readables
final class ReadableReader extends Reader {

  private final Readable in;
  private final Closeable closeableIn;

  ReadableReader(final Readable in) {
    this.in = in;
    this.closeableIn = in instanceof Closeable ? (Closeable) in : NopCloseableFlushable.INSTANCE;
  }

  @Override
  public int read(final char @NotNull [] buffer, final int off, final int len) throws IOException {
    return this.in.read(CharBuffer.wrap(buffer, off, len));
  }

  @Override
  public int read(final @NotNull CharBuffer target) throws IOException {
    return this.in.read(target);
  }

  @Override
  public int read(final char @NotNull [] buff) throws IOException {
    return this.in.read(CharBuffer.wrap(buff));
  }

  @Override
  public void close() throws IOException {
    this.closeableIn.close();
  }
}
