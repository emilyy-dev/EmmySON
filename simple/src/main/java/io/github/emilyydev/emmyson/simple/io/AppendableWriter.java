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
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

// A Writer for Appendables
final class AppendableWriter extends Writer {

  private final Appendable out;
  private final Flushable flushableOut;
  private final Closeable closeableOut;

  AppendableWriter(final Appendable out) {
    this.out = out;
    this.flushableOut = out instanceof Flushable ? (Flushable) out : NopCloseableFlushable.INSTANCE;
    this.closeableOut = out instanceof Closeable ? (Closeable) out : NopCloseableFlushable.INSTANCE;
  }

  @Override
  public void write(final char @NotNull [] buff, final int off, final int len) throws IOException {
    this.out.append(CharBuffer.wrap(buff, off, len), off, off + len);
  }

  @Override
  public void write(final int c) throws IOException {
    this.out.append((char) c);
  }

  @Override
  public void write(final char @NotNull [] buff) throws IOException {
    this.out.append(CharBuffer.wrap(buff));
  }

  @Override
  public void write(final @NotNull String str) throws IOException {
    this.out.append(str);
  }

  @Override
  public void write(final @NotNull String str, final int off, final int len) throws IOException {
    this.out.append(str, off, len);
  }

  @Override
  public Writer append(final CharSequence csq) throws IOException {
    this.out.append(csq);
    return this;
  }

  @Override
  public Writer append(final CharSequence csq, final int start, final int end) throws IOException {
    this.out.append(csq, start, end);
    return this;
  }

  @Override
  public Writer append(final char c) throws IOException {
    this.out.append(c);
    return this;
  }

  @Override
  public void flush() throws IOException {
    this.flushableOut.flush();
  }

  @Override
  public void close() throws IOException {
    this.closeableOut.close();
  }
}
