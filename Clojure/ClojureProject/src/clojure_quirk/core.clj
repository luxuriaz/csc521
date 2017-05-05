(ns clojure-quirk.core
  (:gen-class)
  (:use clojure.pprint)
  (:require [instaparse.core :as insta]))

(defn third [aList] (nth aList 2))
(defn fourth [aList] (nth aList 3))
(defn fifth [aList] (nth aList 4))
(defn sixth [aList] (nth aList 5))
(defn seventh [aList] (nth aList 6))

(defn CallByLabel [funLabel & args]
  (apply (ns-resolve 'clojure-quirk.core (symbol (name funLabel))) args))

(defn ret-print [thingToPrint]
  (println thingToPrint)
  thingToPrint)
;the following functions reduce
;(CallByLabel (first (second subtree)) (second subtree) scope)
;to this:
;(CallByLabel (stSecond subtree) (second subtree) scope)

;    (CallByLabel (stSecond subtree) (second subtree) scope)
;(defn stFirst [subtree] (first (first subtree)))
;(defn stSecond [subtree] (second (first subtree)))

;this return a map with local variable bindings when calling a function
;needs to have a list of values
(defn VarBindLoop [paramList valueList]
  (if (= 1 (count paramList))
    (assoc {} (first paramList)  (first valueList))
    (merge (assoc {} (first paramList)  (first valueList))
           (VarBindLoop
            (rest paramList)
            (rest valueList)))))

;Program
(defn Program [subtree scope]
  (println "Program")
 ;(println subtree)
 ;(println scope)
 ;[:Program [:Statement ...] [:Program ... ]]
 ;[:Program [:Statement ...]]
  (if
   ;Program0
   (< 2 (count subtree))
    ((def newScope (CallByLabel (first (second subtree)) (second subtree) scope))
     (CallByLabel (first (third subtree)) (third subtree) newScope)))
   ;Program1
  (if (>= 2 (count subtree))
    (CallByLabel (first (second subtree)) (second subtree) scope))

  ;(cond
    ;Program0
    ;(= 2(count subtree))
    ;(CallByLabel (first (second subtree)) (second subtree) scope)
    ;Program1
    ;:else
    ;((CallByLabel (first (second subtree)) (second subtree) scope)
    ;  (CallByLabel (first (third subtree)) (third subtree) scope))
   ;)
)

;Statement
(defn Statement [subtree scope]
  ;Statement := FunctionDeclaration | Assignment | Print
  (println "Statement")
  (CallByLabel (first (second subtree)) (second subtree) scope))

(defn FunctionDeclaration [subtree scope]
  (println "FunctionDeclaration")
;FunctionDeclaration := FUNCTION Name LPAREN FunctionParams LBRACE FunctionBody RBRACE
  (def functionName (CallByLabel (first (third subtree)) (third subtree) scope))
  (def paramNames (CallByLabel (first (fifth subtree)) (fifth subtree) scope))
  (def functionBody (CallByLabel (first (seventh subtree)) (seventh subtree) paramNames))
  (if (= [] paramNames)
    (assoc scope functionName functionBody))
;(assoc scope functionName (paramNames (seventh subtree)))
;))
)

(defn FunctionParams [subtree scope]
  ;FunctionParams := NameList RPAREN |RPAREN
  (println "FunctionParams")

  (cond
    (= :RPAREN (first (second subtree)))
    [])) (defn FunctionBody [subtree scope]
  ;FunctionBody := Program Return | Return
           (println "FunctionBody")
           (cond
             (= 3 (count subtree))
             ((CallByLabel (first (second subtree)) (second subtree) scope)
              (CallByLabel (first (third subtree)) (third subtree) scope))
             :else
             (CallByLabel (first (second subtree)) (second subtree) scope)))

(defn Return [subtree scope]
;Return := RETURN ParameterList
  (println "Return")
  (CallByLabel (first (third subtree)) (third subtree) scope))

(defn Assignment [subtree scope]
  ;Assignment := SingleAssignment | MultipleAssignment
  (println "Assignment")
  (CallByLabel (first (second subtree)) (second subtree) scope))

(defn SingleAssignment [subtree scope]
  ;SingleAssignment := VAR Name ASSIGN Expression
  ;  #1. Get name of the variable.
  ;  #2. Get value of <Expression>
  ;  #3. Bind name to value in scope.
  ;  #Bonus: error if the name already exists in scope -- no rebinding
  (println "SingleAssignment")
  (assoc scope  (CallByLabel (first (third subtree)) (third subtree) scope)
         (CallByLabel (first (fifth subtree)) (fifth subtree) scope))
  ;Name: (CallByLabel (first (third subtree)) (third subtree) scope)
  ;Expression:
)

(defn MultipleAssignment [subtree scope]
  ;MultipleAssignment := VAR NameList ASSIGN FunctionCall
  (println "Show You are MultipleAssignment")
  (merge scope (CallByLabel (first (third subtree)) (third subtree) scope)
         (CallByLabel (first (fifth subtree)) (fifth subtree) scope)))

(defn Print [subtree scope]
  (println "Print")
;Print := PRINT Expression
  (ret-print (CallByLabel (first (third subtree)) (third subtree) scope)))

(defn NameList [subtree scope]
  (println "NameList")
;NameList := Name COMMA NameList | Name
  (cond
    (= 4 (count subtree))
    ((CallByLabel (first (second subtree)) (second subtree) scope)
     (CallByLabel (first (third subtree)))
     (CallByLabel (first (fourth subtree)) (fourth subtree) scope))
    :else
    (CallByLabel (first (second subtree)) (second subtree) scope)))

(defn ParameterList [subtree scope]
  (println "ParameterList")
  ;ParameterList := Parameter COMMA ParameterList | Parameter
  (cond
    (= 4 (count subtree))
    ((CallByLabel (first (second subtree)) (second subtree) scope)
     (CallByLabel (first (third subtree)))
     (CallByLabel (first (fourth subtree)) (fourth subtree) scope))
    :else
    (CallByLabel (first (second subtree)) (second subtree) scope)))

(defn Parameter [subtree scope]
  ;Parameter := Expression | Name
  (println "Parameter")
  (CallByLabel (first (second subtree)) (second subtree) scope))

(defn Expression [subtree scope]
  ;Expression := Term ADD Expression | Term SUB Expression | Term
  (println "Expression")

  (cond
    (= 2 (count subtree))
    (CallByLabel (first (second subtree)) (second subtree) scope)
    (= :ADD (first (third subtree)))
    (+ (CallByLabel (first (second subtree)) (second subtree) scope)
       (CallByLabel (first (fourth subtree)) (fourth subtree) scope))
    (= :SUB (first (third subtree)))
    (- (CallByLabel (first (second subtree)) (second subtree) scope)
       (CallByLabel (first (fourth subtree)) (fourth subtree) scope))))

(defn Term [subtree scope]
    ;Term := Factor MULT Term | Factor DIV Term | Factor
  (println "Term")
  (cond
    (= 2 (count subtree))
    (CallByLabel (first (second subtree)) (second subtree) scope)
    (= :MULT (first (third subtree)))
    (* (CallByLabel (first (second subtree)) (second subtree) scope)
       (CallByLabel (first (fourth subtree)) (fourth subtree) scope))
    (= :DIV (first (third subtree)))
    (/ (CallByLabel (first (second subtree)) (second subtree) scope)
       (CallByLabel (first (fourth subtree)) (fourth subtree) scope))))

(defn Factor [subtree scope]
  ;Factor := SubExpression EXP Factor | SubExpression | FunctionCall | Value EXP Factor | Value
  (println "Factor")
  (cond
    (= 4 (count subtree))
    (Math/pow (CallByLabel (first (second subtree)) (second subtree) scope)
              (CallByLabel (first (fourth subtree)) (fourth subtree) scope))
    :else
    (CallByLabel (first (second subtree)) (second subtree) scope)))

(defn FunctionCall [subtree scope]
  ;FunctionCall :=  Name LPAREN FunctionCallParams COLON Number | Name LPAREN FunctionCallParams
  (println "FunctionCall")
  (CallByLabel (first (second subtree)) (second subtree) scope))

(defn FunctionCallParams [subtree scope]
  ;FunctionCallParams :=  ParameterList RPAREN | RPAREN
  (println "FunctionCallParams");(cond
    ;(= 3 (count subtree))
    ;(CallByLabel (first (second subtree)))
    ;:else
    ;((CallByLabel (first (second subtree)) (second subtree) scope)
    ;(CallByLabel (first (second subtree))))
    ;)
)

(defn SubExpression [subtree scope]
  ;SubExpression := LPAREN Expression RPAREN
  (println "SubExpression")
  (CallByLabel (first (third subtree)) (third subtree) scope))

(defn Value [subtree scope]
  ;Value := Name | Number
  (println "Value")
  (CallByLabel (first (second subtree)) (second subtree) scope))

(defn Name [subtree scope]
  (println "Name")

  ;Name := IDENT | SUB IDENT | ADD IDENT
  (cond
    (= nil (get scope (last (second subtree))))
    (last (second subtree))
    :else
    (get scope (second (second subtree)))))

(defn Num [subtree scope]
  (println "Number")
  (Double. (second (second subtree)))
  ;Number := NUMBER | SUB NUMBER | ADD NUMBER
  ;(println subtree)
  ;(def sign (first (second subtree)))
  ;(cond
    ;(= :SUB sign)
    ;(* -1 (Double. (second (second subtree))))
    ;(= :ADD sign)
     ;(Double. (second (second subtree)))
    ;(= 2 (count subtree))
     ;(Double. (second (second subtree)))
    ;)
  ;(System/exit 0)
    )

(defn interpret-quirk [subtree scope]
  (CallByLabel (first subtree) subtree {}))

(defn -main [& args]
  ;sample for reading all stdin
  ;(println stdin)
  ;function foo (x, y) { return 3 + 4 + 2 / x ^ y} print foo(12, 12)
  ;(println (first *command-line-args*))
  (if (.equals "-pt" (first *command-line-args*))
    (def SHOW_PARSE_TREE true))
  (def quirk-parser (insta/parser (slurp "resources/EBNF") :auto-whitespace :standard))
  (def parse-tree (quirk-parser (slurp *in* ))) ;(slurp "resources/example3.q")))
  (if (= true SHOW_PARSE_TREE)
    (pprint parse-tree)
    (interpret-quirk parse-tree {}))
  ;(interpret-quirk parse-tree {})
  ;(println "done!")
)
