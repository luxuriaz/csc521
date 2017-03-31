import sys
import re

#for the lexer
lexemes = []

tokens = {"var":"VAR",
             "function":"FUNCTION",
             "return":"RETRUN",
             "print":"PRINT",
             "=":"ASSIGN",
             "+":"ADD",
             "-":"SUB",
             "*":"MULT",
             "/":"DIV",
             "^":"EXP",
             "(":"LPAREN",
             ")":"RPAREN",
             "{":"LBRACE",
             "}":"RBRACE",
             ",":"COMMA",
             ":":"COLON",}

unvariedLexemes = ["var", "function", "(", "(", "+"];

seperator = ["=", "+", "-", "*", "/", "^", "(", ")", "{", "}", ",", ":"]
def IslineSplit(line):
    for var in seperator:
        if var in line:
            return True;
    return False

def SplitSourceByWhitespace(source):
    allSplits = []
    for line in source:
        thisSplit = line.split()
        for eachline in thisSplit:
            if IslineSplit(eachline):
                tmp_str = ""
                for var in eachline:
                    if var in seperator:
                        if tmp_str != "":
                            allSplits.append(str(tmp_str))
                            tmp_str = ""
                        allSplits.append(str(var))
                    else:
                        tmp_str += var
                if tmp_str != "":
                    allSplits += tmp_str
            else:
                allSplits.append(str(eachline))
    return allSplits

def IsIDENT(line):
    regex = ur"[a-zA-Z]+[a-zA-Z0-9_]*"
    if re.match(regex, line):
        return True
    return False

def IsNUMBER(line):
    regex = ur"((\d+(\.\d*)?)|(\.\d+))"
    if re.match(regex, line):
        return True
    return False

def SplitByUnvariedLexemes(source):
    i=0
    allSplits = []
    while i< len(source):
        line = source[i]
        if line in tokens.keys():
            allSplits.append(tokens[line])
        elif IsIDENT(line):
            allSplits.append("IDENT:" + line)
        elif IsNUMBER(line):
            allSplits.append("NUMBER:" + line)
        i += 1
    print(allSplits)
    return allSplits


def ReadInput():
    lines = sys.stdin.readlines()
    for line in lines:
        print("echo: "+line)



if __name__ == '__main__':
    print ("starting __main__")
    SplitByUnvariedLexemes(SplitSourceByWhitespace(sys.stdin.readlines()))
