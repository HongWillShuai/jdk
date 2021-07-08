/*
 * Copyright (c) 2021, Huawei Technologies Co. Ltd. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
package org.openjdk.bench.java.lang;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

/*
 * This benchmark naively explores String::compare performance
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class StringCompare {
    @Param({"64", "72", "80", "91", "101", "121", "181", "256"})
    int size;

    //@Param({"8", "16", "24", "32", "40", "48", "56", "64", "72", "80", "88", "96", "104", "112", "120"})
    int diff_pos = 0;


    private String str;
    private String strDup;

    @Setup(Level.Trial)
    public void init() {
        str = newString(size, 'c', diff_pos, '1');
        strDup = new String(str.toCharArray());
    }

    public String newString(int length, char charToFill, int diff_pos, char diff_char) {
        if (length > 0) {
            char[] array = new char[length];
            for (int i = 0; i < length; i++) {
                array[i] = charToFill;
            }
            //Arrays.fill(array, charToFill);
            array[diff_pos] = diff_char;
            return new String(array);
        }
        return "";
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int compareLL() {
        int result = 0;
        for (int i = 0; i < 1000; i++) {
            result ^= str.compareTo(strDup);
        }
        return result;
    }

    @Benchmark
    @Fork(jvmArgsAppend = { "-XX:+UseStringCompareWithLdp"})
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int compareLLWithLdp() {
        int result = 0;
        for (int i = 0; i < 1000; i++) {
            result ^= str.compareTo(strDup);
        }
        return result;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {"-XX:-CompactStrings"})
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int compareUU() {
        int result = 0;
        for (int i = 0; i < 1000; i++) {
            result ^= str.compareTo(strDup);
        }
        return result;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {"-XX:-CompactStrings", "-XX:+UseStringCompareWithLdp"})
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int compareUUWithLdp() {
        int result = 0;
        for (int i = 0; i < 1000; i++) {
            result ^= str.compareTo(strDup);
        }
        return result;
    }

}

