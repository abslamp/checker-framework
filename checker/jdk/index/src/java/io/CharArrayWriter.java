/*
 * Copyright (c) 1996, 2005, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.io;
import org.checkerframework.checker.index.qual.*;

import java.util.Arrays;

/**
 * This class implements a character buffer that can be used as an Writer.
 * The buffer automatically grows when data is written to the stream.  The data
 * can be retrieved using toCharArray() and toString().
 * <P>
 * Note: Invoking close() on this class has no effect, and methods
 * of this class can be called after the stream has closed
 * without generating an IOException.
 *
 * @author      Herb Jellinek
 * @since       JDK1.1
 */
public
class CharArrayWriter extends Writer {
    /**
     * The buffer where data is stored.
     */
    protected char buf[];

    /**
     * The number of chars in the buffer.
     */
    protected int count;

    /**
     * Creates a new CharArrayWriter.
     */
    public CharArrayWriter() {
        this(32);
    }

    /**
     * Creates a new CharArrayWriter with the specified initial size.
     *
     * @param initialSize  an int specifying the initial buffer size.
     * @exception IllegalArgumentException if initialSize is negative
     */
    public CharArrayWriter(@NonNegative int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative initial size: "
                                               + initialSize);
        }
        buf = new char[initialSize];
    }

    /**
     * Writes a character to the buffer.
     */
    public void write(@NonNegative int c) {
        synchronized (lock) {
            int newcount = count + 1;
            if (newcount > buf.length) {
                buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
            }
            buf[count] = (char)c;
            count = newcount;
        }
    }

    /**
     * Writes characters to the buffer.
     * @param c the data to be written
     * @param off       the start offset in the data
     * @param len       the number of chars that are written
     */
    public void write(char c[], @IndexFor("#1") int off, @IndexOrHigh("#1") int len) {
        if ((off < 0) || (off > c.length) || (len < 0) ||
            ((off + len) > c.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        synchronized (lock) {
            int newcount = count + len;
            if (newcount > buf.length) {
                buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
            }
            System.arraycopy(c, off, buf, count, len);
            count = newcount;
        }
    }

    /**
     * Write a portion of a string to the buffer.
     * @param  str  String to be written from
     * @param  off  Offset from which to start reading characters
     * @param  len  Number of characters to be written
     */
    public void write(String str, @IndexFor("#1") int off, @IndexOrHigh("#1") int len) {
        synchronized (lock) {
            int newcount = count + len;
            if (newcount > buf.length) {
                buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
            }
            str.getChars(off, off + len, buf, count);
            count = newcount;
        }
    }

    /**
     * Writes the contents of the buffer to another character stream.
     *
     * @param out       the output stream to write to
     * @throws IOException If an I/O error occurs.
     */
    public void writeTo(Writer out) throws IOException {
        synchronized (lock) {
            out.write(buf, 0, count);
        }
    }

    /**
     * Appends the specified character sequence to this writer.
     *
     * <p> An invocation of this method of the form <tt>out.append(csq)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     out.write(csq.toString()) </pre>
     *
     * <p> Depending on the specification of <tt>toString</tt> for the
     * character sequence <tt>csq</tt>, the entire sequence may not be
     * appended. For instance, invoking the <tt>toString</tt> method of a
     * character buffer will return a subsequence whose content depends upon
     * the buffer's position and limit.
     *
     * @param  csq
     *         The character sequence to append.  If <tt>csq</tt> is
     *         <tt>null</tt>, then the four characters <tt>"null"</tt> are
     *         appended to this writer.
     *
     * @return  This writer
     *
     * @since  1.5
     */
    public CharArrayWriter append(CharSequence csq) {
        String s = (csq == null ? "null" : csq.toString());
        write(s, 0, s.length());
        return this;
    }

    /**
     * Appends a subsequence of the specified character sequence to this writer.
     *
     * <p> An invocation of this method of the form <tt>out.append(csq, start,
     * end)</tt> when <tt>csq</tt> is not <tt>null</tt>, behaves in
     * exactly the same way as the invocation
     *
     * <pre>
     *     out.write(csq.subSequence(start, end).toString()) </pre>
     *
     * @param  csq
     *         The character sequence from which a subsequence will be
     *         appended.  If <tt>csq</tt> is <tt>null</tt>, then characters
     *         will be appended as if <tt>csq</tt> contained the four
     *         characters <tt>"null"</tt>.
     *
     * @param  start
     *         The index of the first character in the subsequence
     *
     * @param  end
     *         The index of the character following the last character in the
     *         subsequence
     *
     * @return  This writer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>start</tt> or <tt>end</tt> are negative, <tt>start</tt>
     *          is greater than <tt>end</tt>, or <tt>end</tt> is greater than
     *          <tt>csq.length()</tt>
     *
     * @since  1.5
     */
    public CharArrayWriter append(CharSequence csq, @IndexFor("#1") int start, @IndexOrHigh("#1") int end) {
        String s = (csq == null ? "null" : csq).subSequence(start, end).toString();
        write(s, 0, s.length());
        return this;
    }

    /**
     * Appends the specified character to this writer.
     *
     * <p> An invocation of this method of the form <tt>out.append(c)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     out.write(c) </pre>
     *
     * @param  c
     *         The 16-bit character to append
     *
     * @return  This writer
     *
     * @since 1.5
     */
    public CharArrayWriter append(char c) {
        write(c);
        return this;
    }

    /**
     * Resets the buffer so that you can use it again without
     * throwing away the already allocated buffer.
     */
    public void reset() {
        count = 0;
    }

    /**
     * Returns a copy of the input data.
     *
     * @return an array of chars copied from the input data.
     */
    public char toCharArray()[] {
        synchronized (lock) {
            return Arrays.copyOf(buf, count);
        }
    }

    /**
     * Returns the current size of the buffer.
     *
     * @return an int representing the current size of the buffer.
     */
    public @NonNegative int size() {
        return count;
    }

    /**
     * Converts input data to a string.
     * @return the string.
     */
    public String toString() {
        synchronized (lock) {
            return new String(buf, 0, count);
        }
    }

    /**
     * Flush the stream.
     */
    public void flush() { }

    /**
     * Close the stream.  This method does not release the buffer, since its
     * contents might still be required. Note: Invoking this method in this class
     * will have no effect.
     */
    public void close() { }

}