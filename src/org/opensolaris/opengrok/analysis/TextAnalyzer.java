/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 */
package org.opensolaris.opengrok.analysis;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public abstract class TextAnalyzer extends FileAnalyzer {

    public TextAnalyzer(FileAnalyzerFactory factory) {
        super(factory);
    }

    protected Reader getReader(InputStream stream) throws IOException {
        InputStream in = stream.markSupported() ?
                stream : new BufferedInputStream(stream);

        String charset = null;

        in.mark(3);

        byte[] head = new byte[3];
        int br = in.read(head, 0, 3);

        if (br >= 2
                && (head[0] == (byte) 0xFE && head[1] == (byte) 0xFF)
                || (head[0] == (byte) 0xFF && head[1] == (byte) 0xFE)) {
            charset = "UTF-16";
            in.reset();
        } else if (br >= 3 && head[0] == (byte) 0xEF && head[1] == (byte) 0xBB
                && head[2] == (byte) 0xBF) {
            // InputStreamReader does not properly discard BOM on UTF8 streams,
            // so don't reset the stream.
            charset = "UTF-8";
        }

        if (charset == null) {
            in.reset();
            charset = Charset.defaultCharset().name();
        }

        return new InputStreamReader(in, charset);
    }
}
