export default [
  {
    "category": "%Category.BasicComputation",
    "name": "+",
    "description": "Sum of 2 numeric values"
  },
  {
    "category": "%Category.BasicComputation",
    "name": "-",
    "description": "Subtract 2 numeric values"
  },
  {
    "category": "%Category.BasicComputation",
    "name": "*",
    "description": "Multiply 2 numeric values"
  },
  {
    "category": "%Category.BasicComputation",
    "name": "/",
    "description": "Divide 2 numeric values"
  },
  {
    "category": "%Category.BasicComputation",
    "name": "^",
    "description": "Calculates a number to the nth power, for example 2^6 resolves to 64"
  },
  {
    "category": "%Category.BasicComputation",
    "name": "%",
    "description": "Percentage calculation, for example 12% resolves to 0.12"
  },
  {
    "category": "%Category.BasicComputation",
    "name": "(",
    "description": "Opening bracket to influence calculation order in an expression"
  },
  {
    "category": "%Category.BasicComputation",
    "name": ")",
    "description": "Closing bracket to influence calculation order in an expression"
  },
  {
    "category": "%Category.Comparisons",
    "name": "=",
    "description": "Verify that 2 values are equal"
  },
  {
    "category": "%Category.Comparisons",
    "name": "<>",
    "description": "See if 2 values are different"
  },
  {
    "category": "%Category.Comparisons",
    "name": "<",
    "description": "See if one value is less than the other"
  },
  {
    "category": "%Category.Comparisons",
    "name": ">",
    "description": "See if one value is larger than the other"
  },
  {
    "category": "%Category.Comparisons",
    "name": "<=",
    "description": "See if one value is less than or equal to the other"
  },
  {
    "category": "%Category.Comparisons",
    "name": ">=",
    "description": "See if one value is larger than or equal to the other"
  },
  {
    "category": "%Category.Information",
    "name": "CHOOSE",
    "description": "Uses an index to return a value from a list of values.",
    "syntax": "CHOOSE( Integer Index ; { Any Value }+ )",
    "returns": "Any",
    "constraints": "Returns an error if Index < 1 or if there is no corresponding value in the list of Values.",
    "semantics": "Uses Index to determine which value, from a list of values, to return. If Index is 1, CHOOSE returns the first Value; if Index is 2, CHOOSE returns the second value, and so on. Note that the Values may be formula expressions. Expression paths of parameters other than the one chosen are not calculated or evaluated for side effects.",
    "examples": {
      "example": [
        {
          "expression": "CHOOSE(3;\"Apple\";\"Orange\";\"Grape\";\"Perry\")",
          "result": "\"Grape\"",
          "level": "1",
          "comment": "Simple selection."
        },
        {
          "expression": "CHOOSE(0;\"Apple\";\"Orange\";\"Grape\";\"Perry\")",
          "result": "Error",
          "level": "1",
          "comment": "Index has to be at least 1."
        },
        {
          "expression": "CHOOSE(5;\"Apple\";\"Orange\";\"Grape\";\"Perry\")",
          "result": "Error",
          "level": "1",
          "comment": "Index can't refer to non-existent entry."
        },
        {
          "expression": "CHOOSE(2;SUM([.B4:.B5]);SUM([.B5]))",
          "result": "3",
          "level": "1",
          "comment": "Simple selection, using a set of formulas."
        },
        {
          "expression": "SUM(CHOOSE(2;[.B4:.B5];[.B5]))",
          "result": "3",
          "level": "1",
          "comment": "CHOOSE can pass references"
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISBLANK",
    "description": "Return TRUE if the referenced cell is blank, else return FALSE",
    "syntax": "ISBLANK( Scalar X )",
    "returns": "Logical",
    "constraints": "None",
    "semantics": "If X is of type Number, Text, or Logical, return FALSE. If X is a reference to a cell, examine the cell; if it is blank (has no value), return TRUE, but if it has a value, return FALSE. A cell with the empty string is not considered blank.",
    "examples": {
      "example": [
        {
          "expression": "ISBLANK(1)",
          "result": "False",
          "level": "1",
          "comment": "Numbers return false."
        },
        {
          "expression": "ISBLANK(\"\")",
          "result": "False",
          "level": "1",
          "comment": "Text, even empty string, returns false."
        },
        {
          "expression": "ISBLANK([.B8])",
          "result": "True",
          "level": "1",
          "comment": "Blank cell is true."
        },
        {
          "expression": "ISBLANK([.B7])",
          "result": "False",
          "level": "1",
          "comment": "Non-blank cell is false."
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISERR",
    "description": "Return True if the parameter has type Error and is not NA, else return False.",
    "syntax": "ISERR( Scalar X )",
    "returns": "Logical",
    "constraints": "None",
    "semantics": " If X is of type Error, and ISNA(X) is not true, returns TRUE. Otherwise it returns FALSE. Note that this function returns False if given NA(); if this is not desired, use ISERROR. Note that this function does not propagate error values.\nISERR(X) is the same as:\nIF(ISNA(X),FALSE(),ISERROR(X))",
    "examples": {
      "example": [
        {
          "expression": "ISERR(1/0)",
          "result": "True",
          "level": "1",
          "comment": "Error values other than NA() return true."
        },
        {
          "expression": "ISERR(NA())",
          "result": "False",
          "level": "1",
          "comment": "NA() does NOT return True."
        },
        {
          "expression": "ISERR(\"#N/A\")",
          "result": "False",
          "level": "1",
          "comment": "Text is not an error."
        },
        {
          "expression": "ISERR(1)",
          "result": "False",
          "level": "1",
          "comment": "Numbers are not an error."
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISERROR",
    "description": "Return TRUE if the parameter has type Error, else return FALSE",
    "syntax": "ISERROR( Scalar X )",
    "returns": "Logical",
    "constraints": "None",
    "semantics": "If X is of type Error, returns TRUE, else returns FALSE. Note that this function returns True if given NA(); if this is not desired, use ISERR. Note that this function does not propagate error values.",
    "examples": {
      "example": [
        {
          "expression": "ISERROR(1/0)",
          "result": "True",
          "level": "1",
          "comment": "Error values return true."
        },
        {
          "expression": "ISERROR(NA())",
          "result": "True",
          "level": "1",
          "comment": "Even NA()."
        },
        {
          "expression": "ISERROR(\"#N/A\")",
          "result": "False",
          "level": "1",
          "comment": "Text is not an error."
        },
        {
          "expression": "ISERROR(1)",
          "result": "False",
          "level": "1",
          "comment": "Numbers are not an error."
        },
        {
          "expression": "ISERROR(CHOOSE(0; \"Apple\"; \"Orange\"; \"Grape\"; \"Perry\"))",
          "result": "True",
          "level": "1",
          "comment": "If CHOOSE given out-of-range value, ISERROR needs to capture it."
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISEVEN",
    "description": "Return TRUE if the value is even, else return FALSE",
    "syntax": "ISEVEN( Number X )",
    "returns": "Logical",
    "constraints": "X must not be Logical",
    "semantics": "First, compute X1=TRUNC(X). Then, if X is even (a division by 2 has a remainder of 0), return True, else return False. The result is implementation-defined if given a logical value; an application may return either an Error or the result of converting the logical value to a number (per Conversion to Number).",
    "examples": {
      "example": [
        {
          "expression": "ISEVEN(2)",
          "result": "True",
          "level": "3",
          "comment": "2 is even, because (2 modulo 2) = 0"
        },
        {
          "expression": "ISEVEN(6)",
          "result": "True",
          "level": "3",
          "comment": "6 is even, because (6 modulo 2) = 0"
        },
        {
          "expression": "ISEVEN(2.1)",
          "result": "True",
          "level": "3"
        },
        {
          "expression": "ISEVEN(2.5)",
          "result": "True",
          "level": "3"
        },
        {
          "expression": "ISEVEN(2.9)",
          "result": "True",
          "level": "3",
          "comment": "TRUNC(2.9)=2, and 2 is even."
        },
        {
          "expression": "ISEVEN(3)",
          "result": "False",
          "level": "3",
          "comment": "3 is not even."
        },
        {
          "expression": "ISEVEN(3.9)",
          "result": "False",
          "level": "3",
          "comment": "TRUNC(3.9)=3, and 3 is not even."
        },
        {
          "expression": "ISEVEN(-2)",
          "result": "True",
          "level": "3"
        },
        {
          "expression": "ISEVEN(-2.1)",
          "result": "True",
          "level": "3"
        },
        {
          "expression": "ISEVEN(-2.5)",
          "result": "True",
          "level": "3"
        },
        {
          "expression": "ISEVEN(-2.9)",
          "result": "True",
          "level": "3",
          "comment": "TRUNC(-2.9)=-2, and -2 is even."
        },
        {
          "expression": "ISEVEN(-3)",
          "result": "False",
          "level": "3"
        },
        {
          "expression": "ISEVEN(NA())",
          "result": "NA",
          "level": "3"
        },
        {
          "expression": "ISEVEN(0)",
          "result": "True",
          "level": "3"
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISLOGICAL",
    "description": "Return TRUE if the parameter has type Logical, else return FALSE",
    "syntax": "ISLOGICAL( Scalar X )",
    "returns": "Logical",
    "constraints": "None",
    "semantics": "If X is of type Logical, returns TRUE, else FALSE. For applications that do not have a distinct logical type, also ISNUMBER(X) will return TRUE.",
    "examples": {
      "example": [
        {
          "expression": "ISLOGICAL(TRUE())",
          "result": "True",
          "level": "1",
          "comment": "Logical values return true."
        },
        {
          "expression": "ISLOGICAL(FALSE())",
          "result": "True",
          "level": "1",
          "comment": "Logical values return true."
        },
        {
          "expression": "ISLOGICAL(\"TRUE\")",
          "result": "False",
          "level": "1",
          "comment": "Text values are not logicals, even if they can be converted."
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISNA",
    "description": "Return True if the parameter is of type NA, else return False.",
    "syntax": "ISERR( Scalar X )",
    "returns": "Logical",
    "constraints": "None",
    "semantics": "If X is NA, return True, else return False. Note that if X is a reference, the value being referenced is considered. This function does not propagate error values.",
    "examples": {
      "example": [
        {
          "expression": "ISNA(1/0)",
          "result": "False",
          "level": "1",
          "comment": "Error values other than NA() return False – the error does not propagate."
        },
        {
          "expression": "ISNA(NA())",
          "result": "True",
          "level": "1",
          "comment": "By definition"
        },
        {
          "expression": "ISNA(#N/A)",
          "result": "True",
          "level": "1",
          "comment": "By definition"
        },
        {
          "expression": "ISNA(\"#N/A\")",
          "result": "False",
          "level": "1",
          "comment": "Text is not NA"
        },
        {
          "expression": "ISNA(1)",
          "result": "False",
          "level": "1",
          "comment": "Numbers are not NA"
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISNONTEXT",
    "description": "Return TRUE if the parameter does not have type Text, else return FALSE",
    "syntax": "ISNONTEXT( Scalar X )",
    "returns": "Logical",
    "constraints": "None",
    "semantics": " If X is of type Text, returns TRUE, else FALSE. If X is a reference, examines what X references. References to blank cells are NOT considered text, so a reference to a blank cell will return TRUE.\nISNONTEXT(X) is the same as:\nNOT(ISTEXT(X))",
    "examples": {
      "example": [
        {
          "expression": "ISNONTEXT(1)",
          "result": "True",
          "level": "1",
          "comment": "Numbers are not text"
        },
        {
          "expression": "ISNONTEXT(TRUE())",
          "result": "True",
          "level": "1",
          "comment": "Logical values are not text."
        },
        {
          "expression": "ISNONTEXT(\"1\")",
          "result": "False",
          "level": "1",
          "comment": "Text values are text, even if they can be converted into a number."
        },
        {
          "expression": "ISNONTEXT([.B7])",
          "result": "False",
          "level": "1",
          "comment": "B7 is a cell with text"
        },
        {
          "expression": "ISNONTEXT([.B9])",
          "result": "True",
          "level": "1",
          "comment": "B9 is an error, thus not text"
        },
        {
          "expression": "ISNONTEXT([.B8])",
          "result": "True",
          "level": "1",
          "comment": "B8 is a blank cell, so this will return TRUE"
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISNUMBER",
    "description": "Return TRUE if the parameter has type Number, else return FALSE",
    "syntax": "ISNUMBER( Scalar X )",
    "returns": "Logical",
    "constraints": "None",
    "semantics": " If X is of type Number, returns TRUE, else FALSE. Level 1 implementations may not have a distinguished logical type; in such implementations, ISNUMBER(TRUE()) is TRUE.",
    "examples": {
      "example": [
        {
          "expression": "ISNUMBER(1)",
          "result": "True",
          "level": "1",
          "comment": "Numbers are numbers"
        },
        {
          "expression": "ISNUMBER(\"1\")",
          "result": "False",
          "level": "1",
          "comment": "Text values are not numbers, even if they can be converted into a number."
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISODD",
    "description": "Return TRUE if the value is even, else return FALSE",
    "syntax": "ISODD( Number X )",
    "returns": "Logical",
    "constraints": "X must not be Logical",
    "semantics": "First, compute X1=TRUNC(X). Then, if X is odd (a division by 2 has a remainder of 1), return True, else return False. The result is implementation-defined if given a logical value; an application may return either an Error or the result of converting the logical value to a number (per Conversion to Number).",
    "examples": {
      "example": [
        {
          "expression": "ISODD(3)",
          "result": "True",
          "level": "3",
          "comment": "3 is odd, because (3 modulo 2) = 1"
        },
        {
          "expression": "ISODD(5)",
          "result": "True",
          "level": "3",
          "comment": "5 is odd, because (5 modulo 2) = 1"
        },
        {
          "expression": "ISODD(3.1)",
          "result": "True",
          "level": "3",
          "comment": "TRUNC(3.1)=3, and 3 is odd"
        },
        {
          "expression": "ISODD(3.5)",
          "result": "True",
          "level": "3",
          "comment": "3 is odd."
        },
        {
          "expression": "ISODD(3.9)",
          "result": "True",
          "level": "3",
          "comment": "TRUNC(3.9)=3, and 3 is odd."
        },
        {
          "expression": "ISODD(4)",
          "result": "False",
          "level": "3"
        },
        {
          "expression": "ISODD(4.9)",
          "result": "False",
          "level": "3"
        },
        {
          "expression": "ISODD(-3)",
          "result": "True",
          "level": "3"
        },
        {
          "expression": "ISODD(-3.1)",
          "result": "True",
          "level": "3"
        },
        {
          "expression": "ISODD(-3.5)",
          "result": "True",
          "level": "3"
        },
        {
          "expression": "ISODD(-3.9)",
          "result": "True",
          "level": "3",
          "comment": "TRUNC(-3.9)=-3, and -3 is odd."
        },
        {
          "expression": "ISODD(-4)",
          "result": "False",
          "level": "3"
        },
        {
          "expression": "ISODD(NA())",
          "result": "NA",
          "level": "3"
        },
        {
          "expression": "ISODD(0)",
          "result": "False",
          "level": "3"
        },
        {
          "expression": "ISODD(1)",
          "result": "True",
          "level": "3"
        },
        {
          "expression": "ISODD(2)",
          "result": "False",
          "level": "3"
        },
        {
          "expression": "ISODD(2.9)",
          "result": "False",
          "level": "3"
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISREF",
    "description": "Return True if the parameter is of type reference, else return False.",
    "syntax": "ISREF( Any X )",
    "returns": "Logical",
    "constraints": "None",
    "semantics": " If X is of type Reference or ReferenceList, return True, else return False. Note that unlike nearly all other functions, when given a reference this function does not then examine the value being referenced. Some functions and operators return references, and thus ISREF will return True when given their results. X may be a ReferenceList, in which case ISREF returns True.",
    "examples": {
      "example": [
        {
          "expression": "ISREF([.B3])",
          "result": "True",
          "level": "1"
        },
        {
          "expression": "ISREF([.B3]:[.C4])",
          "result": "True",
          "level": "1",
          "comment": "The range operator produces references"
        },
        {
          "expression": "ISREF(1)",
          "result": "False",
          "level": "1",
          "comment": "Numbers are not references"
        },
        {
          "expression": "ISREF(\"A1\")",
          "result": "False",
          "level": "1",
          "comment": "Text is not a reference, even if it looks a little like one"
        },
        {
          "expression": "ISREF(NA())",
          "result": "NA",
          "level": "1",
          "comment": "Errors propagate through this function"
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "ISTEXT",
    "description": "Return TRUE if the parameter has type Text, else return FALSE",
    "syntax": "ISTEXT( Scalar X )",
    "returns": "Logical",
    "constraints": "None",
    "semantics": "If X is of type Text, returns TRUE, else FALSE. References to blank cells are NOT considered text.",
    "examples": {
      "example": [
        {
          "expression": "ISTEXT(1)",
          "result": "False",
          "level": "1",
          "comment": "Numbers are not text"
        },
        {
          "expression": "ISTEXT(\"1\")",
          "result": "True",
          "level": "1",
          "comment": "Text values are text, even if they can be converted into a number."
        }
      ]
    }
  },
  {
    "category": "%Category.Information",
    "name": "NA",
    "description": "Return the constant error value #N/A.",
    "syntax": "NA()",
    "returns": "Error",
    "constraints": "Must have 0 parameters",
    "semantics": "This function takes no arguments and returns the error NA.",
    "examples": {
      "example": [
        {
          "expression": "ISERROR(NA())",
          "result": "True",
          "level": "1",
          "comment": "NA is an error."
        },
        {
          "expression": "ISNA(NA())",
          "result": "True",
          "level": "1",
          "comment": "Obviously, if this doesn't work, NA() or ISNA() is broken."
        },
        {
          "expression": "ISNA(5+NA())",
          "result": "True",
          "level": "1",
          "comment": "NA propagates through various functions and operators, just like any other error type."
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "&",
    "description": "Concatenate two strings.",
    "syntax": "Text Left & Text Right",
    "returns": "Text",
    "constraints": "None",
    "semantics": "Concatenates two text (string) values. Due to the way conversion works, numbers are converted to strings. Note that this is equivalent to CONCATENATE(Left,Right). (Note: CONCATENATE is not yet available in libformula version 0.1.18.2)",
    "examples": {
      "example": [
        {
          "expression": "\"Hi \" & \"there\"",
          "result": "\"Hi there\"",
          "level": "1",
          "comment": "Simple concatenation."
        },
        {
          "expression": "\"H\" & \"\"",
          "result": "\"H\"",
          "level": "1",
          "comment": "Concatenating an empty string produces no change."
        },
        {
          "expression": "-5&\"b\"",
          "result": "“-5b”",
          "level": "1",
          "comment": "Unary “-” has higher precedence than “&”"
        },
        {
          "expression": "3&2-1",
          "result": "“31”",
          "level": "1",
          "comment": "Binary “-” has higher precedence than “&”"
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "EXACT",
    "description": "Report if two text values are exactly equal using a case-sensitive comparison",
    "syntax": "EXACT( Text t1 ; Text t2 )",
    "returns": "Logical",
    "constraints": "None",
    "semantics": "Converts both sides to text, and then returns TRUE if the two text values are \"exactly\" equal, including case, otherwise it returns FALSE.",
    "examples": {
      "example": [
        {
          "expression": "EXACT(\"A\";\"A\")",
          "result": "True",
          "level": "1",
          "comment": "Trivial comparison."
        },
        {
          "expression": "EXACT(\"A\";\"a\")",
          "result": "False",
          "level": "1",
          "comment": "EXACT, unlike \"=\", considers different cases different."
        },
        {
          "expression": "EXACT(1;1)",
          "result": "True",
          "level": "1",
          "comment": "EXACT does work with numbers."
        },
        {
          "expression": "EXACT((1/3)*3;1)",
          "result": "True",
          "level": "1",
          "comment": "Numerical comparisons ignore \"trivial\" differences that depend only on numeric precision of finite numbers."
        },
        {
          "expression": "EXACT(TRUE();TRUE())",
          "result": "True",
          "level": "1",
          "comment": "Works with Logical values."
        },
        {
          "expression": "EXACT(\"1\";2)",
          "result": "False",
          "level": "1",
          "comment": "Different types with different values are different."
        },
        {
          "expression": "EXACT(\"h\";1)",
          "result": "False",
          "level": "1",
          "comment": "If text and number, and text can't be converted to a number, they are different and NOT an error."
        },
        {
          "expression": "EXACT(\"1\";1)",
          "result": "True",
          "level": "1",
          "comment": "If text and number, see if number converted to text is equal."
        },
        {
          "expression": "EXACT(“ 1”;1)",
          "result": "False",
          "level": "1",
          "comment": "This converts 1 into the Text value “1”, the compares and finds that it's not the same as “ 1” (note the leading space)."
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "FIND",
    "description": "Return the starting position of a given text.",
    "syntax": "FIND( Text Search ; Text T [ ; Integer Start = 1 ] )",
    "returns": "Number",
    "constraints": "Start >= 1",
    "examples": {
      "example": [
        {
          "expression": "FIND(\"b\";\"abcabc\")",
          "result": "2",
          "level": "1",
          "comment": "Simple FIND()"
        },
        {
          "expression": "FIND(\"b\";\"abcabcabc\"; 3)",
          "result": "5",
          "level": "1",
          "comment": "Start changes the start of the search"
        },
        {
          "expression": "FIND(\"b\";\"ABC\";1)",
          "result": "Error",
          "level": "1",
          "comment": "Matching is case-sensitive."
        },
        {
          "expression": "FIND(\"b\";\"bbbb\")",
          "result": "1",
          "level": "1",
          "comment": "Simple FIND(), default is 1"
        },
        {
          "expression": "FIND(\"b\";\"bbbb\";2)",
          "result": "2",
          "level": "1"
        },
        {
          "expression": "FIND(\"b\";\"bbbb\";2.9)",
          "result": "2",
          "level": "1",
          "comment": "INT(Start) used as starting position"
        },
        {
          "expression": "FIND(\"b\";\"bbbb\";0)",
          "result": "Error",
          "level": "1",
          "comment": "Start >= 0"
        },
        {
          "expression": "FIND(\"b\";\"bbbb\";0.9)",
          "result": "Error",
          "level": "1"
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "LEFT",
    "description": "Return a selected number of text characters from the left.",
    "syntax": "LEFT( Text T [ ; Integer Length ] )",
    "returns": "Text",
    "constraints": "Length >= 0",
    "semantics": "Returns the INT(Length) number of characters of text T, starting from the left. If Length is omitted, it defaults to 1; otherwise, it computes Length=INT(Length). If T has fewer than Length characters, it returns T. This means that if T is an empty string (which has length 0) or the parameter Length is 0, LEFT() will always return an empty string. Note that if Length<0, an Error is returned. This function must return the same string as MID(T; 1; Length).\n  \t",
    "examples": {
      "example": [
        {
          "expression": "LEFT(\"Hello\";2)",
          "result": "\"He\"",
          "level": "1",
          "comment": "Simple LEFT()."
        },
        {
          "expression": "LEFT(\"Hello\";2.9)",
          "result": "\"He\"",
          "level": "1",
          "comment": "INT(), not round to nearest or round towards positive infinity, must be used to convert length into an integer."
        },
        {
          "expression": "LEFT(\"Hello\")",
          "result": "\"H\"",
          "level": "1",
          "comment": "Length defaults to 1."
        },
        {
          "expression": "LEFT(\"Hello\";20)",
          "result": "\"Hello\"",
          "level": "1",
          "comment": "If Length is longer than T, returns T."
        },
        {
          "expression": "LEFT(\"Hello\";0)",
          "result": "\"\"",
          "level": "2",
          "comment": "If Length 0, returns empty string."
        },
        {
          "expression": "LEFT(\"\";4)",
          "result": "\"\"",
          "level": "1",
          "comment": "Given an empty string, always returns empty string."
        },
        {
          "expression": "LEFT(\"xxx\";-0.1)",
          "result": "Error",
          "level": "1",
          "comment": "It makes no sense to request a negative number of characters. Also, this tests to ensure that INT() is used to convert non-integers to integers; if -0.1 were incorrectly rounded to 0 (as it would be by round-to-nearest or round-toward-zero), this would incorrectly return a null string."
        },
        {
          "expression": "LEFT(\"Hello\";2^15-1)",
          "result": "\"Hello\"",
          "level": "1",
          "comment": "If Length > LEN(T) entire string is returned."
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "LEN",
    "description": "Return the length, in characters, of given text",
    "syntax": "LEN( Text T )",
    "returns": "Integer",
    "constraints": "None.",
    "semantics": "Computes number of characters (not the number of bytes) in T. Implementations that support ISO 10646 / Unicode shall consider any character in the Basic Multilingual Plane (BMP) basic plane as one character, even if they occupy multiple bytes. (The BMP are the characters numbered 0 through 65535 inclusive). Implementations should consider any character not in the BMP as one character as well.",
    "examples": {
      "example": [
        {
          "expression": "LEN(\"Hi There\")",
          "result": "8",
          "level": "1",
          "comment": "Space is a character."
        },
        {
          "expression": "LEN(\"\")",
          "result": "0",
          "level": "1",
          "comment": "Empty string has zero characters."
        },
        {
          "expression": "LEN(55)",
          "result": "2",
          "level": "1",
          "comment": "Numbers are automatically converted."
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "LOWER",
    "description": "Return input string, but with all uppercase letters converted to lowercase letters.",
    "syntax": "LOWER( Text T )",
    "returns": "Text",
    "constraints": "None",
    "semantics": "Return input string, but with all uppercase letters converted to lowercase letters. As with most functions, it is side-effect free (it does not modify the source values). All implementations shall convert A-Z to a-z. However, as this function can be locale aware, results may be unexpected in certain cases.  For example in a Turkish locale an upper case \"I without dot\" U+0049 is converted to a lower case \"i without dot\" U+0131.",
    "examples": {
      "example": {
        "expression": "LOWER(\"HELLObc7\")",
        "result": "\"hellobc7\"",
        "level": "1",
        "comment": "Uppercase converted to lowercase; other characters just copied to result."
      }
    }
  },
  {
    "category": "%Category.Text",
    "name": "MID",
    "description": "Returns extracted text, given an original text, starting position, and length.",
    "syntax": "MID( Text T ; Integer Start ; Integer Length )",
    "returns": "Text",
    "constraints": "Start >= 1, Length >= 0.",
    "semantics": "Returns the characters from T, starting at character position Start, for up to Length characters. For the integer conversions, Start=INT(Start), and Length=INT(Length). If there are less than Length characters starting at start, it returns as many characters as it can beginning with Start. In particular, if Start > LEN(T), it returns the empty string (\"\"). If Start < 0, it returns an Error. If Start >=0, and Length=0, it returns the empty string. Note that MID(T;1;Length) produces the same results as LEFT(T;Length).",
    "examples": {
      "example": [
        {
          "expression": "MID(\"123456789\";5;3)",
          "result": "\"567\"",
          "level": "1",
          "comment": "Simple use of MID."
        },
        {
          "expression": "MID(\"123456789\";20;3)\n\t   \t",
          "result": "\"\"",
          "level": "1",
          "comment": "If Start is beyond string, return empty string."
        },
        {
          "expression": "MID(\"123456789\";-1;0)\n\t   \t",
          "result": "Error",
          "level": "1",
          "comment": "Start cannot be less than one; even if the length is 0"
        },
        {
          "expression": "MID(\"123456789\";1;0)\n\t   \t",
          "result": "\"\"",
          "level": "1",
          "comment": "But otherwise, length=0 produces the empty string"
        },
        {
          "expression": "MID(\"123456789\";2.9;1)\n\t   \t",
          "result": "\"2\"",
          "level": "1",
          "comment": "INT(Start) is used"
        },
        {
          "expression": "MID(\"123456789\";2;2.9)\n\t   \t",
          "result": "\"23\"",
          "level": "1",
          "comment": "INT(Length) is used"
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "REPLACE",
    "description": "Returns text where an old text is substituted with a new text.",
    "syntax": "REPLACE( Text T ; Number Start ; Number Len ; Text New )",
    "returns": "Text",
    "constraints": "Start >= 1.",
    "semantics": "Returns text T, but remove the characters starting at character position Start for Len characters, and instead replace them with New. Character positions defined by Start begin at 1 (for the leftmost character). If Len=0, the text New is inserted before character position Start, and all the text before and after Start is retained.",
    "examples": {
      "example": [
        {
          "expression": "REPLACE(\"123456789\";5;3;\"Q\")",
          "result": "\"1234Q89\"",
          "level": "1",
          "comment": "Replacement text may have different length."
        },
        {
          "expression": "REPLACE(\"123456789\";5;0;\"Q\")",
          "result": "\"1234Q56789\"",
          "level": "1",
          "comment": "If Len=0, 0 characters removed."
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "REPT",
    "description": "Return text repeated Count times.",
    "syntax": "REPT( Text T ; Integer Count )",
    "returns": "Text",
    "constraints": "Count >= 0",
    "semantics": "Returns text T repeated Count number of times; if Count is zero, an empty string is returned. If Count < 0, the result is Error.",
    "examples": {
      "example": [
        {
          "expression": "REPT(\"X\";3)",
          "result": "\"XXX\"",
          "level": "1",
          "#text": "Simple REPT."
        },
        {
          "expression": "REPT(\"XY\";2)",
          "result": "\"XYXY\"",
          "level": "1",
          "comment": "Repeated text can have length > 1."
        },
        {
          "expression": "REPT(\"X\";2.9)",
          "result": "\"XX\"",
          "level": "1",
          "comment": "INT(Count) used if count is a fraction"
        },
        {
          "expression": "REPT(\"X\";0)",
          "result": "\"\"",
          "level": "1",
          "comment": "If Count is zero, empty string"
        },
        {
          "expression": "REPT(\"X\";-1)",
          "result": "Error",
          "level": "1",
          "comment": "If Count is negative, Error."
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "RIGHT",
    "description": "Return a selected number of text characters from the right.",
    "syntax": "RIGHT( Text T [ ; Integer Length ] )",
    "returns": "Text",
    "constraints": "Length >= 0",
    "semantics": "Returns the Length number of characters of text T, starting from the right. If Length is omitted, it defaults to 1; otherwise, it computes Length=INT(Length). If T has fewer than Length characters, it returns T (unchanged). This means that if T is an empty string (which has length 0) or the parameter Length is 0, RIGHT() will always return an empty string. Note that if Length<0, an Error is returned.",
    "examples": {
      "example": [
        {
          "expression": "RIGHT(\"Hello\";2)",
          "result": "\"lo\"",
          "level": "1",
          "comment": "Simple RIGHT()."
        },
        {
          "expression": "RIGHT(\"Hello\")",
          "result": "\"o\"",
          "level": "1",
          "comment": "Length defaults to 1."
        },
        {
          "expression": "RIGHT(\"Hello\";20)",
          "result": "\"Hello\"",
          "level": "1",
          "comment": "If Length is longer than T, returns T."
        },
        {
          "expression": "RIGHT(\"Hello\";0)",
          "result": "\"\"",
          "level": "1",
          "comment": "If Length 0, returns empty string."
        },
        {
          "expression": "RIGHT(\"Hello\";2^15-1)",
          "result": "“Hello”",
          "level": "1",
          "comment": "If Length is larger than T and is very large, it still returns the original short string."
        },
        {
          "expression": "RIGHT(\"\";4)",
          "result": "\"\"",
          "level": "1",
          "comment": "Given an empty string, always returns empty string."
        },
        {
          "expression": "RIGHT(\"Hello\";-1)",
          "result": "Error",
          "level": "1",
          "comment": "It makes no sense to request a negative number of characters."
        },
        {
          "expression": "RIGHT(\"Hello\";-0.1)",
          "result": "Error",
          "level": "1",
          "comment": "Must use INT, not round-to-nearest or round-towards zero, to convert Length to Integer"
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "SUBSTITUTE",
    "description": "Returns text where an old text is substituted with a new text.",
    "syntax": "SUBSTITUTE( Text T ; Text Old ; Text New [ ; Number Which ] )",
    "returns": "Text",
    "constraints": "Which >= 1 (when provided)",
    "semantics": "Returns text T, but with text Old replaced by text New (when searching from the left). If Which is omitted, every occurrence of Old is replaced with New; if Which is provided, only that occurrence of Old is replaced by New (starting the count from 1). If there is no match, or if Old has length 0, the value of T is returned. Note that Old and New may have different lengths. If Which is present and Which < 1, returns Error.",
    "examples": {
      "example": [
        {
          "expression": "SUBSTITUTE(\"121212\";\"2\";\"ab\")",
          "result": "\"1ab1ab1ab\"",
          "level": "1",
          "comment": "Without Which, all replaced."
        },
        {
          "expression": "SUBSTITUTE(\"121212\";\"2\";\"ab\";2)",
          "result": "\"121ab12\"",
          "level": "1",
          "comment": "Which starts counting from 1."
        },
        {
          "expression": "SUBSTITUTE(\"Hello\";\"x\";\"ab\")",
          "result": "\"Hello\"",
          "level": "1",
          "comment": "If not found, returns unchanged."
        },
        {
          "expression": "SUBSTITUTE(\"xyz\";\"\";\"ab\")",
          "result": "\"xyz\"",
          "level": "1",
          "comment": "Returns T if Old is Length 0."
        },
        {
          "expression": "SUBSTITUTE(\"\";\"\";\"ab\")",
          "result": "\"\"",
          "level": "1",
          "comment": "Returns T if Old is Length 0, even if T is empty (it does not consider an empty T to “match” an empty Old)."
        },
        {
          "expression": "SUBSTITUTE(\"Hello\"; \"H\"; \"J\"; 0)",
          "result": "Error",
          "level": "1",
          "comment": "Which cannot be less than 1."
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "T",
    "description": "Return the text (if text), else return 0-length Text value",
    "syntax": "T( Any X )",
    "returns": "Text",
    "constraints": "None",
    "semantics": "The type of (a dereferenced) X is examined; if it is of type Text, it is returned, else an empty string (Text value of zero length) is returned. This is not a type-conversion function; T(5) produces an empty string, not \"5\".",
    "examples": {
      "example": [
        {
          "expression": "T(\"HI\")",
          "result": "\"HI\"",
          "level": "1",
          "comment": "T does not change text."
        },
        {
          "expression": "T([.B3])",
          "result": "\"7\"",
          "level": "1",
          "comment": "References transformed into what they reference."
        },
        {
          "expression": "T(5)",
          "result": "\"\"",
          "level": "1",
          "comment": "Non-text converted into null string."
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "TEXT",
    "description": "Return the value converted to a text.",
    "syntax": "TEXT( Scalar X ; Text FormatCode )",
    "returns": "Text",
    "constraints": "The FormatCode is a sequence of characters with an application-defined meaning.\nPortable Contraints: The result of this function may change across locales. If separators such as decimal or group separator are involved, conversion may give unexpected results if the separators don't match that of the current locale. Across applications the result may change to the extend to which number format codes and their subtleties are supported. Portable documents should not use this function.",
    "semantics": "Converts the value X to a text according to the rules of a number format code passed as FormatCode and returns it.",
    "examples": {
      "example": [
        {
          "expression": "TEXT(12345.6789;\"#,##0.00\")",
          "result": "\"12,345.68\"",
          "level": "3",
          "comment": "Non-text converted to text. This is locale-specific."
        },
        {
          "expression": "TEXT(3;\"0\"\" good things\"\"\")",
          "result": "\"3 good things\"",
          "level": "3"
        }
      ]
    }
  },
  {
    "category": "%Category.Text",
    "name": "TRIM",
    "description": "Remove leading and trailing spaces, and replace all internal multiple spaces with a single space.",
    "syntax": "TRIM( Text T )",
    "returns": "Text",
    "constraints": "None",
    "semantics": "Takes T and removes all leading and trailing space. Any other sequence of 2 or more spaces is replaced with a single space.",
    "examples": {
      "example": {
        "expression": "TRIM(\" ABC  \")",
        "result": "\"ABC\"",
        "level": "1"
      }
    }
  },
  {
    "category": "%Category.Text",
    "name": "UPPER",
    "description": "Return input string, but with all lowercase letters converted to uppercase letters.",
    "syntax": "UPPER( Text T )",
    "returns": "Text",
    "constraints": "None",
    "semantics": "Return input string, but with all lowercase letters converted to uppercase letters. As with most functions, it is side-effect free (it does not modify the source values). All implementations shall convert a-z to A-Z. However, as this function can be locale aware, results may be unexpected in certain cases, for example in a Turkish locale a lower case \"i with dot\" U+0069 is converted to an upper case \"I with dot\" U+0130.",
    "examples": {
      "example": {
        "expression": "UPPER(\"Habc7\")",
        "result": "\"HABC7\"",
        "level": "1",
        "comment": "Lowercase converted to upper case; other characters just copied to result."
      }
    }
  },
  {
    "category": "%Category.Mathematical",
    "name": "INT",
    "description": "Rounds a number down to the nearest integer.",
    "syntax": "INT( Number N )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Returns the nearest integer whose value is less than or equal to N. Rounding is towards negative infinity.",
    "examples": {
      "example": [
        {
          "expression": "INT(2)",
          "result": "2",
          "level": "1",
          "comment": "Positive integers remain unchanged"
        },
        {
          "expression": "INT(-3)",
          "result": "-3",
          "level": "1",
          "comment": "Negative integers remain unchanged"
        },
        {
          "expression": "INT(1.2)",
          "result": "1",
          "level": "1",
          "comment": "Positive floating values are truncated"
        },
        {
          "expression": "INT(1.7)",
          "result": "1",
          "level": "1",
          "comment": "It doesn’t matter if the fractional part is > 0.5"
        },
        {
          "expression": "INT(-1.2)",
          "result": "-2",
          "level": "1",
          "comment": "Negative floating values round towards negative infinity"
        },
        {
          "expression": "INT((1/3)*3)",
          "result": "1",
          "level": "1",
          "comment": "Naive users expect INT to \"correctly\" make integers even if there are limits on precision."
        }
      ]
    }
  },
  {
    "category": "%Category.Mathematical",
    "name": "ABS",
    "description": "Return the absolute (nonnegative) value.",
    "syntax": "ABS( Number N )",
    "returns": "Number",
    "constraints": "None",
    "semantics": " If N < 0, returns -N, otherwise returns N.",
    "examples": {
      "example": [
        {
          "expression": "ABS(-4)",
          "result": "4",
          "level": "1",
          "comment": "If less than zero, return negation"
        },
        {
          "expression": "ABS(4)",
          "result": "4",
          "level": "1",
          "comment": "Positive values return unchanged."
        }
      ]
    }
  },
  {
    "category": "%Category.Mathematical",
    "name": "AVERAGE",
    "description": "Average the set of numbers",
    "syntax": "AVERAGE( { NumberSequence N }+ )",
    "returns": "Number",
    "constraints": "At least one number included. Returns an error if no numbers provided.",
    "semantics": "Computes SUM(List) / COUNT(List).",
    "examples": {
      "example": {
        "expression": "AVERAGE(2;4)",
        "result": "3",
        "level": "1",
        "comment": "Simple average"
      }
    }
  },
  {
    "category": "%Category.Mathematical",
    "name": "EVEN",
    "description": "Rounds a number up to the nearest even integer. Rounding is away from zero.",
    "syntax": "EVEN( Number N )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Returns the even integer whose sign is the same as N's and whose absolute value is greater than or equal to the absolute value of N. That is, if rounding is required, it is rounded away from zero.",
    "examples": {
      "example": [
        {
          "expression": "EVEN(6)",
          "result": "6",
          "level": "1",
          "comment": "Positive even integers remain unchanged."
        },
        {
          "expression": "EVEN(-4)",
          "result": "-4",
          "level": "1",
          "comment": "Negative even integers remain unchanged."
        },
        {
          "expression": "EVEN(1)",
          "result": "2",
          "level": "1",
          "comment": "Non-even positive integers round up."
        },
        {
          "expression": "EVEN(0.3)",
          "result": "2",
          "level": "1",
          "comment": "Positive floating values round up."
        },
        {
          "expression": "EVEN(-1)",
          "result": "-2",
          "level": "1",
          "comment": "Non-even negative integers round down."
        },
        {
          "expression": "EVEN(-0.3)",
          "result": "-2",
          "level": "1",
          "comment": "Negative floating values round down."
        },
        {
          "expression": "EVEN(0)",
          "result": "0",
          "level": "1",
          "comment": "Since zero is even, EVEN(0) returns zero."
        }
      ]
    }
  },
  {
    "category": "%Category.Mathematical",
    "name": "MAX",
    "description": "Return the maximum from a set of numbers.",
    "syntax": "MAX( { NumberSequenceList N } )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Returns the value of the maximum number in the list passed in. Non-numbers are ignored. Note that if logical types are a distinct type, they are not included. What happens when MAX is provided 0 parameters is implementation-defined, but MAX with no parameters should return 0.",
    "examples": {
      "example": [
        {
          "expression": "MAX(2;4;1;-8)",
          "result": "4",
          "level": "1",
          "comment": "Negative numbers are smaller than positive numbers."
        },
        {
          "expression": "MAX([.B4:.B5])",
          "result": "3",
          "level": "1",
          "comment": "The maximum of (2,3) is 3."
        }
      ]
    }
  },
  {
    "category": "%Category.Mathematical",
    "name": "MIN",
    "description": "Return the minimum from a set of numbers.",
    "syntax": "MIN( { NumberSequenceList N } )",
    "returns": "Number",
    "constraints": "None.",
    "semantics": "Returns the value of the minimum number in the list passed in. Returns zero if no numbers are provided in the list. What happens when MIN is provided 0 parameters is implementation-defined, but MIN() with no parameters should return 0.",
    "examples": {
      "example": [
        {
          "expression": "MIN(2;4;1;-8)",
          "result": "-8",
          "level": "1",
          "comment": "Negative numbers are smaller than positive numbers."
        },
        {
          "expression": "MIN([.B4:.B5])",
          "result": "2",
          "level": "1",
          "comment": "The minimum of (2,3) is 2."
        },
        {
          "expression": "MIN([.B3])",
          "result": "0",
          "level": "1",
          "comment": "If no numbers are provided in all ranges, MIN returns 0"
        },
        {
          "expression": "MIN(\"a\")",
          "result": "Error",
          "level": "1",
          "comment": "Non-numbers inline are NOT ignored."
        },
        {
          "expression": "MIN([.B3:.B5])",
          "result": "2",
          "level": "1",
          "comment": "Cell text is not converted to numbers and is ignored."
        }
      ]
    }
  },
  {
    "category": "%Category.Mathematical",
    "name": "ODD",
    "description": "Rounds a number up to the nearest odd integer, where \"up\" means \"away from 0\".",
    "syntax": "ODD( Number N )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Returns the odd integer whose sign is the same as N's and whose absolute value is greater than or equal to the absolute value of N. In other words, any \"rounding\" is away from zero. By definition, ODD(0) is 1.",
    "examples": {
      "example": [
        {
          "expression": "ODD(5)",
          "result": "5",
          "level": "1",
          "comment": "Positive odd integers remain unchanged."
        },
        {
          "expression": "ODD(-5)",
          "result": "-5",
          "level": "1",
          "comment": "Negative odd integers remain unchanged."
        },
        {
          "expression": "ODD(2)",
          "result": "3",
          "level": "1",
          "comment": "Non-odd positive integers round up."
        },
        {
          "expression": "ODD(0.3)",
          "result": "1",
          "level": "1",
          "comment": "Positive floating values round up."
        },
        {
          "expression": "ODD(-2)",
          "result": "-3",
          "level": "1",
          "comment": "Non-odd negative integers round down."
        },
        {
          "expression": "ODD(-0.3)",
          "result": "-1",
          "level": "1",
          "comment": "Negative floating values round down."
        },
        {
          "expression": "ODD(0)",
          "result": "1",
          "level": "1",
          "comment": "By definition, ODD(0) is 1."
        }
      ]
    }
  },
  {
    "category": "%Category.Mathematical",
    "name": "SUM",
    "description": "Sum (add) the set of numbers, including all numbers in ranges",
    "syntax": "SUM( { NumberSequenceList N }+ )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Adds numbers (and only numbers) together (see the text on conversions). Applications may allow SUM to receive 0 parameters (and return 0), but portable documents must not depend on SUM() with zero parameters returning 0.",
    "examples": {
      "example": [
        {
          "expression": "SUM(1;2;3)",
          "result": "6",
          "level": "1",
          "comment": "Simple sum."
        },
        {
          "expression": "SUM(TRUE();2;3)",
          "result": "6",
          "level": "1",
          "comment": "TRUE() is 1."
        },
        {
          "expression": "SUM([.B4:.B5])",
          "result": "5",
          "level": "1",
          "comment": "2+3 is 5."
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "DATE",
    "description": "Construct date from year, month, and day of month.",
    "syntax": "DATE( Integer Year ; Integer Month ; Integer Day )",
    "returns": "Date",
    "constraints": "1 <= Month <= 12; 1 <= Day <= 31",
    "semantics": "This computes the date's serial number given Year, Month, and Day. Fractional values are truncated. The value of the serial number depends on the current epoch. Note that some applications may not handle correctly dates before 1904; in particular, many spreadsheets incorrectly claim that 1900 is a leap year (it was not; there was no 1900-02-29).",
    "examples": {
      "example": [
        {
          "expression": "DATE(2005;1;31)=[.C7]",
          "result": "True",
          "level": "1",
          "comment": "Simple date value."
        },
        {
          "expression": "DATE(2005;12;31)-DATE(1904;1;1)",
          "result": "37255",
          "level": "1",
          "comment": "Date differences are computed correctly."
        },
        {
          "expression": "DATE(2004;2;29)=DATE(2004;2;28)+1",
          "result": "True",
          "level": "1",
          "comment": "2004 was a leap year."
        },
        {
          "expression": "DATE(2000;2;29)=DATE(2000;2;28)+1",
          "result": "True",
          "level": "1",
          "comment": "2000 was a leap year."
        },
        {
          "expression": "DATE(2005;3;1)=DATE(2005;2;28)+1",
          "result": "True",
          "level": "1",
          "comment": "2005 was not a leap year."
        },
        {
          "expression": "DATE(2017.5; 1; 2)=DATE(2017; 1; 2)",
          "result": "True",
          "level": "1",
          "comment": "Fractional values for year are truncated"
        },
        {
          "expression": "DATE(2006; 2.5; 3)=DATE(2006; 2; 3)",
          "result": "True",
          "level": "1",
          "comment": "Fractional values for month are truncated"
        },
        {
          "expression": "DATE(2006; 1; 3.5)=DATE(2006; 1; 3)",
          "result": "True",
          "level": "1",
          "comment": "Fractional values for day are truncated"
        },
        {
          "expression": "DATE(2006; 13; 3)=DATE(2007; 1; 3)",
          "result": "True",
          "level": "1",
          "comment": "Months > 12 roll over to year "
        },
        {
          "expression": "DATE(2006; 1; 32)=DATE(2006; 2; 1)",
          "result": "True",
          "level": "1",
          "comment": "Days greater than month limit roll over to month"
        },
        {
          "expression": "DATE(2006; 25; 34)=DATE(2008;2;3)",
          "result": "True",
          "level": "1",
          "comment": "Days and months roll over transitively "
        },
        {
          "expression": "DATE(2006;-1; 1)=DATE(2005;11;1)",
          "result": "True",
          "level": "1",
          "comment": "Negative months roll year backward"
        },
        {
          "expression": "DATE(2006;4;-1)=DATE(2006;3;30)",
          "result": "True",
          "level": "1",
          "comment": "Negative days roll month backward"
        },
        {
          "expression": "DATE(2006;-4;-1)=DATE(2005;7;30)",
          "result": "True",
          "level": "1",
          "comment": "Negative days and months roll backward transitively"
        },
        {
          "expression": "DATE(2003;2;29)=DATE(2003;3;1)",
          "result": "True",
          "level": "1",
          "comment": "Non-leap year rolls forward"
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "DATEDIF",
    "description": "Return the number of years, months, or days between two date numbers.",
    "syntax": "DATEDIF( DateParam StartDate ; DateParam EndDate ; Text Format )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Compute difference between StartDate and EndDate, in the units given by Format.\nThe Format is a code from the following table, entered as text, that specifies the format you want the result of DATEDIF to have:\ny : Years\nm : Months. If there is not a complete month between the dates, 0 will be returned.\nd : Days\nmd : Days, ignoring months and years\nym : Months, ignoring years\nyd : Days, ignoring years\n  \t",
    "examples": {
      "example": [
        {
          "expression": "DATEDIF(DATE(1990;2;15); DATE(1993;9;15); \"y\")",
          "level": "3"
        },
        {
          "expression": "DATEDIF(DATE(1990;2;15); DATE(1993;9;15); \"m\")",
          "result": "43",
          "level": "3",
          "comment": "The number of months between February 15, 1990, and September 15, 1993."
        },
        {
          "expression": "DATEDIF(DATE(1990;2;15); DATE(1993;9;15); \"d\")",
          "level": "3"
        },
        {
          "expression": "DATEDIF(DATE(1990;2;15); DATE(1993;9;15); \"md\")",
          "result": "0",
          "level": "3",
          "comment": "The day of the month for both start-date and end-date is the 15th"
        },
        {
          "expression": "DATEDIF(DATE(1990;2;15); DATE(1993;9;15); \"ym\")",
          "result": "7",
          "level": "3",
          "comment": "The number of months between February and September."
        },
        {
          "expression": "DATEDIF(DATE(1990;2;15); DATE(1993;9;15); \"yd\")"
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "DATEVALUE",
    "description": "Return date serial number from given text",
    "syntax": "DATEVALUE( Text D )",
    "returns": "Date",
    "constraints": "None",
    "semantics": "This computes the serial number of the text string D, using the current locale. This function must accept ISO date format (YYYY-MM-DD), which is locale-independent. It is semantically equal VALUE(Date) if Date has a date format, since text matching a date format is automatically converted to a serial number when used as a Number. If the text of D has a combined date and time format, e.g. YYYY-MM-DD HH:MM:SS, the integer part of the date serial number is returned. If the text of Date does not have a date or time format, an implementation may return an error. See VALUE for more information on date formats.\nIn an OpenDocument file, the calculation settings table:null-year and table:null-date affect this function.",
    "examples": {
      "example": [
        {
          "expression": "DATEVALUE(\"2004-12-25\")=DATE(2004;12;25)",
          "result": "True",
          "level": "2",
          "comment": "DATEVALUE"
        },
        {
          "expression": "DATEVALUE(\"2004-12-25 12:34:56\")=DATE(2004;12;25)",
          "result": "True",
          "level": "2",
          "comment": "Only the integer part is returned"
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "DAY",
    "description": "Extract the day from a date.",
    "syntax": "DAY( DateParam Date )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Returns the day portion of the date.",
    "examples": {
      "example": [
        {
          "expression": "DAY(DATE(2006;5;21))",
          "result": "21",
          "level": "1",
          "comment": "Basic extraction."
        },
        {
          "expression": "DAY(\"2006-12-15\")",
          "result": "12",
          "level": "1",
          "comment": "Text allowed too, since it's a DateParam"
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "HOUR",
    "description": "Extract the hour (0 through 23) from a time.",
    "syntax": "HOUR( TimeParam T )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Semantics: Extract from T the hour value, 0 through 23, as per a 24-hour clock. This is equal to:\nDayFraction=(T-INT(T))\nHour=INT(DayFraction*24)",
    "examples": {
      "example": [
        {
          "expression": "HOUR(5/24)",
          "result": "5",
          "level": "1",
          "comment": "5/24ths of a day is 5 hours, aka 5AM."
        },
        {
          "expression": "HOUR(5/24-1/(24*60*60))",
          "result": "4",
          "level": "1",
          "comment": "A second before 5AM, it's 4AM."
        },
        {
          "expression": "HOUR(\"14:00\")",
          "result": "14",
          "level": "1",
          "comment": "TimeParam accepts text"
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "MONTH",
    "description": "Extract the month from a date",
    "syntax": "MONTH( DateParam Date )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Takes a date and returns the month portion.",
    "examples": {
      "example": [
        {
          "expression": "MONTH([.C7])",
          "result": "1",
          "level": "1",
          "comment": "Month extraction from date in cell."
        },
        {
          "expression": "MONTH(DATE(2006;5;21))",
          "result": "5",
          "level": "1",
          "comment": "Month extraction from DATE() value."
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "NOW",
    "description": "Return the serial number of the current date and time.",
    "syntax": "NOW()",
    "returns": "DateTime",
    "constraints": "None",
    "semantics": "This returns the current day and time serial number, using the current locale. If you want only the serial number of the current day, use TODAY.",
    "examples": {
      "example": [
        {
          "expression": "NOW()>DATE(2006;1;3)",
          "result": "True",
          "level": "1",
          "comment": "NOW constantly changes, but we know it's beyond this date."
        },
        {
          "expression": "INT(NOW())=TODAY()",
          "result": "True",
          "level": "1",
          "comment": "NOW() is part of TODAY(). WARNING: this test is allowed to fail if the locale transitions through midnight while computing this test; this failure is incredibly unlikely to occur in practice."
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "TIME",
    "description": "Construct time from hours, minutes, and seconds.",
    "syntax": "TIME( Number hours ; Number minutes ; Number seconds )",
    "returns": "Time",
    "constraints": "None",
    "semantics": "Returns the fraction of the day consumed by the given time, i.e.:\n((hours*60*60)+(minutes*60)+seconds)/(24*60*60)\nTime is a subtype of number, where a time value of 1 = 1 day = 24 hours. Note that the time inside one day is a fraction between 0 and 1, so typical implementations will only be able to compute approximations of the correct time value.\nImplementations may first perform INT() on the hour, minute, and second before doing the calculation. Therefore, only integer values are portable between implementations. Hours, minutes, and seconds may be arbitrary numbers (they must not be limited to the ranges 0..24, 0..59, or 0..60 respectively).\nNote that in typical implementations, a value displayed as time has its integer portion discarded and then time is computed; for computational purposes, though, the entire value is retained.",
    "examples": {
      "example": [
        {
          "expression": "TIME(0;0;0)",
          "result": "0",
          "level": "1",
          "comment": "All zero arguments becomes midnight, 12:00:00 AM."
        },
        {
          "expression": "TIME(23;59;59)*60*60*24",
          "result": "86399±ε",
          "level": "1",
          "comment": "This is 11:59:59 PM."
        },
        {
          "expression": "TIME(11;125;144)*60*60*24",
          "result": "47244±ε",
          "level": "1",
          "comment": "Seconds and minutes roll over transitively; this is 1:07:24 PM."
        },
        {
          "expression": "TIME(11;0; -117)*60*60*24",
          "result": "39483±ε",
          "level": "1",
          "comment": "Negative seconds roll minutes backwards, 10:58:03 AM"
        },
        {
          "expression": "TIME(11;-117;0)*60*60*24",
          "result": "32580±ε",
          "level": "1",
          "comment": "Negative minutes roll hours backwards, 9:03:00 AM"
        },
        {
          "expression": "TIME(11;-125;-144)*60*60*24",
          "result": "-31956±ε",
          "level": "1",
          "comment": "Negative seconds and minutes roll backwards transitively, 8:52:36 AM"
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "TODAY",
    "description": "Return the serial number of today",
    "syntax": "TODAY()",
    "returns": "Date",
    "constraints": "None",
    "semantics": "This returns the current day's serial number, using current locale. This only returns the date, not the datetime value; if you need the specific time of day as well, use NOW().",
    "examples": {
      "example": [
        {
          "expression": "TODAY()>DATE(2006;1;3)",
          "result": "True",
          "level": "1",
          "comment": "Every date TODAY() changes, but we know it's beyond this date."
        },
        {
          "expression": "INT(TODAY())=TODAY()",
          "result": "True",
          "level": "1",
          "comment": "TODAY() returns an integer. WARNING: this test is allowed to fail if the locale transitions through midnight while computing this test; because TODAY() is referenced twice, in some implementations this would result in a race condition) This is incredibly unlikely to occur in practice."
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "WEEKDAY",
    "description": "Extract the day of the week from a date; if text, uses current locale to convert to a date.",
    "syntax": "WEEKDAY( DateParam Date [ ; Integer Type = 1 ] )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Returns the day of the week from a date, as a number from 0 through 7. The exact meaning depends on the value of Type:\n1.When Type is 1, Sunday is the first day of the week, with value 1; Saturday has value 7.\n2.When Type is 2, Monday is the first day of the week, with value 1; Sunday has value 7.\n3.When Type is 3, Monday is the first day of the week, with value 0; Sunday has value 6.\n  \t",
    "examples": {
      "example": [
        {
          "expression": "WEEKDAY(DATE(2006;5;21))",
          "result": "1",
          "level": "1",
          "comment": "Year-month-date format"
        },
        {
          "expression": "WEEKDAY(DATE(2005;1;1))",
          "result": "7",
          "level": "1",
          "comment": "Saturday."
        },
        {
          "expression": "WEEKDAY(DATE(2005;1;1);1)",
          "result": "7",
          "level": "1",
          "comment": "Saturday."
        },
        {
          "expression": "WEEKDAY(DATE(2005;1;1);2)",
          "result": "6",
          "level": "1",
          "comment": "Saturday."
        },
        {
          "expression": "WEEKDAY(DATE(2005;1;1);3)",
          "result": "5",
          "level": "1",
          "comment": "Saturday."
        }
      ]
    }
  },
  {
    "category": "%Category.DateTime",
    "name": "YEAR",
    "description": "Extract the year from a date given in the current locale of the application.",
    "syntax": "YEAR( DateParam D )",
    "returns": "Number",
    "constraints": "None",
    "semantics": "Parses a date-formatted string in the current locale's format and returns the year portion.\nIf a year is given as a two-digit number, as in \"05-21-15\", then the year returned is either 1915 or 2015, depending upon the a break point in the calculation context.  In an OpenDocument document, this break point is determined by table:null-year.\nApplications shall support extracting the year from a date beginning in 1900. Three-digit year numbers precede adoption of the Gregorian calendar, and may return either an error or the year number. Four-digit year numbers preceding 1582 (inception of the Gregorian Calendar) may return either an error or the year number. Four-digit year numbers following 1582 should return the year number.",
    "examples": {
      "example": {
        "expression": "YEAR(DATE(1904;1;1))",
        "result": "1904",
        "level": "1",
        "comment": "Extracts year from a given date."
      }
    }
  },
  {
    "category": "%Category.Logical",
    "name": "AND",
    "returns": "Logical",
    "examples": {
      "example": {
        "level": "1"
      }
    }
  },
  {
    "category": "%Category.Logical",
    "name": "AND",
    "description": "Compute logical AND of all parameters.",
    "syntax": "AND( { Logical|NumberSequenceList L }+ )",
    "returns": "Logical",
    "constraints": "Must have 1 or more parameters",
    "semantics": "Computes the logical AND of the parameters. If all parameters are True, returns True; if any are False, returns False. When given one parameter, this has the effect of converting that one parameter into a logical value. When given zero parameters, applications may return a Logical value or an error.\nAlso in array context a logical AND of all arguments is computed, range or array parameters are not evaluated as a matrix and no array is returned. This behavior is consistent with functions like SUM. To compute a logical AND of arrays per element use the * operator in array context.",
    "examples": {
      "example": [
        {
          "expression": "AND(FALSE();FALSE())",
          "result": "False",
          "level": "1",
          "comment": "Simple AND."
        },
        {
          "expression": "AND(FALSE();TRUE())",
          "result": "False",
          "level": "1",
          "comment": "Simple AND."
        },
        {
          "expression": "AND(TRUE();FALSE())",
          "result": "False",
          "level": "1",
          "comment": "Simple AND."
        },
        {
          "expression": "AND(TRUE();TRUE())",
          "result": "True",
          "level": "1",
          "comment": "Simple AND."
        },
        {
          "expression": "AND(TRUE();NA())",
          "result": "NA",
          "level": "1",
          "comment": "Returns an error if given one."
        },
        {
          "expression": "AND(1;TRUE())",
          "result": "True",
          "level": "1",
          "comment": "Nonzero considered TRUE."
        },
        {
          "expression": "AND(0;TRUE())",
          "result": "False",
          "level": "1",
          "comment": "Zero considered FALSE."
        },
        {
          "expression": "AND(TRUE();TRUE();TRUE())",
          "result": "True",
          "level": "1",
          "comment": "More than two parameters okay."
        },
        {
          "expression": "AND(TRUE())",
          "result": "True",
          "level": "1",
          "comment": "One parameter okay - simply returns it."
        }
      ]
    }
  },
  {
    "category": "%Category.Logical",
    "name": "IF",
    "description": "Return one of two values, depending on a condition",
    "syntax": "IF( Logical Condition [ ; [ Any IfTrue ] [ ; [ Any IfFalse ] ] ] )",
    "returns": "Any",
    "constraints": "None.",
    "semantics": "Computes Condition. If it is TRUE, it returns IfTrue, else it returns IfFalse. If there is only 1 parameter, IfTrue is considered to be TRUE(). If there are less than 3 parameters, IfFalse is considered to be FALSE(). Thus the 1 parameter version converts Condition into a Logical value. If there are 2 or 3 parameters but the second parameter is null (two consecutive ; semicolons), IfFalse is considered to be 0. If there are 3 parameters but the third parameter is null, IfFalse is considered to be 0. This function only evaluates IfTrue, or ifFalse, and never both; that is to say, it short-circuits.",
    "examples": {
      "example": [
        {
          "expression": "IF(FALSE();7;8)",
          "result": "8",
          "level": "1",
          "comment": "Simple if."
        },
        {
          "expression": "IF(TRUE();7;8)",
          "result": "7",
          "level": "1",
          "comment": "Simple if."
        },
        {
          "expression": "IF(TRUE();\"HI\";8)",
          "result": "\"HI\"",
          "level": "1",
          "comment": "Can return strings, and the two sides need not have equal types"
        },
        {
          "expression": "IF(1;7;8)",
          "result": "7",
          "level": "1",
          "comment": "A non-zero is considered true."
        },
        {
          "expression": "IF(5;7;8)",
          "result": "7",
          "level": "1",
          "comment": "A non-zero is considered true."
        },
        {
          "expression": "IF(0;7;8)",
          "result": "8",
          "level": "1",
          "comment": "A zero is considered false."
        },
        {
          "expression": "IF(TRUE();[.B4];8)",
          "result": "2",
          "level": "1",
          "comment": "The result can be a reference."
        },
        {
          "expression": "IF(TRUE();[.B4]+5;8)",
          "result": "7",
          "level": "1",
          "comment": "The result can be a formula."
        },
        {
          "expression": "IF(\"x\";7;8)",
          "result": "Error",
          "level": "1",
          "comment": "Condition has to be convertible to Logical."
        },
        {
          "expression": "IF(\"1\";7;8)",
          "result": "Error",
          "level": "1",
          "comment": "Condition has to be convertible to Logical."
        },
        {
          "expression": "IF(\"\";7;8)",
          "result": "Error",
          "level": "1",
          "comment": "Condition has to be convertible to Logical; empty string is not the same as False"
        },
        {
          "expression": "IF(FALSE();7)",
          "result": "FALSE",
          "level": "1",
          "comment": "Default IfFalse is FALSE"
        },
        {
          "expression": "IF(3)",
          "result": "TRUE",
          "level": "1",
          "comment": "Default IfTrue is TRUE"
        },
        {
          "expression": "IF(FALSE();7;)",
          "result": "0",
          "level": "1",
          "comment": "Empty parameter is considered 0"
        },
        {
          "expression": "IF(TRUE();7)",
          "result": "0",
          "level": "1",
          "comment": "Empty parameter is considered 0"
        },
        {
          "expression": "IF(TRUE();4;1/0)",
          "result": "4",
          "level": "1",
          "comment": "If condition is true, ifFalse is not considered – even if it would produce Error."
        },
        {
          "expression": "IF(FALSE();1/0;5)",
          "result": "5",
          "level": "1",
          "comment": "If condition is false, ifTrue is not considered – even if it would produce Error."
        }
      ]
    }
  },
  {
    "category": "%Category.Logical",
    "name": "NOT",
    "description": "Compute logical NOT",
    "syntax": "NOT( Logical L )",
    "returns": "Logical",
    "constraints": "Must have 1 parameter",
    "semantics": "Computes the logical NOT. If given TRUE, returns FALSE; if given FALSE, returns TRUE.",
    "examples": {
      "example": [
        {
          "expression": "NOT(FALSE())",
          "result": "True",
          "level": "1",
          "comment": "Simple NOT, given FALSE."
        },
        {
          "expression": "NOT(TRUE())",
          "result": "False",
          "level": "1",
          "comment": "Simple NOT, given TRUE."
        },
        {
          "expression": "NOT(1/0)",
          "result": "Error",
          "level": "1",
          "comment": "NOT returns an error if given an error value"
        }
      ]
    }
  },
  {
    "category": "%Category.Logical",
    "name": "OR",
    "description": "Compute logical OR of all parameters.",
    "syntax": "OR( { Logical|NumberSequenceList L }+ )",
    "returns": "Logical",
    "constraints": "Must have 1 or more parameters",
    "semantics": "Computes the logical OR of the parameters. If all parameters are False, it shall return False; if any are True, it shall returns True. When given one parameter, this has the effect of converting that one parameter into a logical value. When given zero parameters, applications may return a Logical value or an error.\nAlso in array context a logical OR of all arguments is computed, range or array parameters are not evaluated as a matrix and no array is returned. This behavior is consistent with functions like SUM. To compute a logical OR of arrays per element use the + operator in array context.",
    "examples": {
      "example": [
        {
          "expression": "OR(FALSE();FALSE())",
          "result": "False",
          "level": "1",
          "comment": "Simple OR."
        },
        {
          "expression": "OR(FALSE();TRUE())",
          "result": "True",
          "level": "1",
          "comment": "Simple OR."
        },
        {
          "expression": "OR(TRUE();FALSE())",
          "result": "True",
          "level": "1",
          "comment": "Simple OR."
        },
        {
          "expression": "OR(TRUE();TRUE())",
          "result": "True",
          "level": "1",
          "comment": "Simple OR."
        },
        {
          "expression": "OR(FALSE();NA())",
          "result": "NA",
          "level": "1",
          "comment": "Returns an error if given one."
        },
        {
          "expression": "OR(FALSE();FALSE();TRUE())",
          "result": "True",
          "level": "1",
          "comment": "More than two parameters okay."
        },
        {
          "expression": "OR(TRUE())",
          "result": "True",
          "level": "1",
          "comment": "One parameter okay - simply returns it"
        }
      ]
    }
  },
  {
    "category": "%Category.Logical",
    "name": "TRUE",
    "description": "Returns constant TRUE",
    "syntax": "TRUE()",
    "returns": "Logical",
    "constraints": "Must have 0 parameters",
    "semantics": "Returns logical constant TRUE. Although this is syntactically a function call, semantically it is a constant, and typical applications optimize this because it is a constant. Note that this may or may not be equal to 1 when compared using “=”. It always has the value of 1 if used in a context requiring Number (because of the automatic conversions), so if ISNUMBER(TRUE()), then it must have the value 1.",
    "examples": {
      "example": [
        {
          "expression": "TRUE()",
          "result": "True",
          "level": "1",
          "comment": "Constant."
        },
        {
          "expression": "IF(ISNUMBER(TRUE());TRUE()=1;TRUE())",
          "result": "True",
          "level": "1",
          "comment": "Applications that implement logical values as 0/1 must map TRUE() to 1"
        },
        {
          "expression": "2+TRUE()",
          "result": "3",
          "level": "1",
          "comment": "TRUE converts to 1 in Number context"
        }
      ]
    }
  },
  {
    "category": "%Category.Logical",
    "name": "XOR",
    "description": "Compute a logical XOR of all parameters.",
    "syntax": "XOR( { Logical L }+ )",
    "returns": "Logical",
    "constraints": "Must have 1 or more parameters.",
    "semantics": "Computes the logical XOR of the parameters such that the result is an addition modulo 2. If an even number of parameters is True it returns False, if an odd number of parameters is True it returns True. When given one parameter, this has the effect of converting that one parameter into a logical value.\nNote: The multi-argument form is different from an \"exclusive disjunction\" operation, which would return True if and only if exactly one argument is True.",
    "examples": {
      "example": [
        {
          "expression": "XOR(FALSE();FALSE())",
          "result": "False",
          "level": "1",
          "comment": "Simple XOR."
        },
        {
          "expression": "XOR(FALSE();TRUE())",
          "result": "True",
          "level": "1",
          "comment": "Simple XOR."
        },
        {
          "expression": "XOR(TRUE();FALSE())",
          "result": "True",
          "level": "1",
          "comment": "Simple XOR."
        },
        {
          "expression": "XOR(TRUE();TRUE())",
          "result": "False",
          "level": "1",
          "comment": "Simple XOR – note that this one is different from OR"
        },
        {
          "expression": "XOR(FALSE();NA())",
          "result": "NA",
          "level": "1",
          "comment": "Returns an error if given one."
        },
        {
          "expression": "XOR(FALSE();FALSE();TRUE())",
          "result": "True",
          "level": "1",
          "comment": "More than two parameters okay."
        },
        {
          "expression": "XOR(FALSE(); TRUE();TRUE())",
          "result": "False",
          "level": "1",
          "comment": "More than two parameters okay, and notice that this result is different from OR"
        },
        {
          "expression": "XOR(TRUE(); TRUE();TRUE())",
          "result": "True",
          "level": "1",
          "comment": "More than two parameters okay, the result is ((1 XOR 1) XOR 1), thus a parity."
        },
        {
          "expression": "XOR(TRUE())",
          "result": "True",
          "level": "1",
          "comment": "One parameter okay - simply returns it"
        }
      ]
    }
  }
]
