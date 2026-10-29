/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/** */
package org.mozilla.javascript.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.testutils.Utils;

/**
 * @author Ronald Brill
 */
public class NativeJsonTest {

    @Test
    public void stringifyResultAndResultType() {
        String jsScript =
                "function f(){ return JSON.stringify({property1:\"hello\", array1:[{subobject:1}]}); } f();";

        try (Context cx = Context.enter()) {
            TopLevel jsScope = cx.initStandardObjects();
            Object result = cx.evaluateString(jsScope, jsScript, "myscript.js", 1, null);
            assertEquals("{\"property1\":\"hello\",\"array1\":[{\"subobject\":1}]}", result);
            assertEquals("java.lang.String", result.getClass().getName());
        }
    }

    @Test
    public void reviverDoesNotReplaceNonConfigurableProperty() {
        Utils.assertWithAllModes_ES6(
                "2,2",
                Utils.lines(
                        "function reviver(k, v) {",
                        "  var key = Array.isArray(this) ? 1 : 'b';",
                        "  if (v === 1) Object.defineProperty(this, key, {configurable: false});",
                        "  return v === 2 ? 22 : v;",
                        "}",
                        "var obj = JSON.parse(JSON.stringify({a: 1, b: 2}), reviver);",
                        "var arr = JSON.parse('[1, 2]', reviver);",
                        "obj.b + ',' + arr[1]"));
    }

    @Test
    public void reviverDoesNotThrowOnNonConfigurablePropertyInStrictMode() {
        Utils.assertWithAllModes_ES6(
                "2,2",
                Utils.lines(
                        "'use strict';",
                        "function reviver(k, v) {",
                        "  var key = Array.isArray(this) ? 1 : 'b';",
                        "  if (v === 1) Object.defineProperty(this, key, {configurable: false});",
                        "  return v === 2 ? undefined : v;",
                        "}",
                        "var obj = JSON.parse(JSON.stringify({a: 1, b: 2}), reviver);",
                        "var arr = JSON.parse('[1, 2]', reviver);",
                        "obj.b + ',' + arr[1]"));
    }
}
