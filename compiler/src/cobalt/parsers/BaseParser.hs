{-|
Module      : BaseParser
Description : Contains parsing functions used in all other parsers.
Generally a lower level parser for words etc.
-}
module BaseParser (Parser, scn, symbol, integer, rword, rws, parens, word, identifier) where

import Control.Applicative (empty)
import Control.Monad (void)
import Data.Void
import Data.Char (isAlphaNum)
import Text.Megaparsec
import Text.Megaparsec.Char
import qualified Text.Megaparsec.Char.Lexer as L
import Text.Megaparsec.Expr
import Text.Pretty.Simple (pShow)

type Parser = Parsec Void String


lineComment :: Parser ()
lineComment = L.skipLineComment "#"


scn :: Parser ()
scn = L.space space1 lineComment empty


sc :: Parser ()
sc = L.space (void $ takeWhile1P Nothing f) lineComment empty
  where
    f x = x == ' ' || x == '\t'


symbol :: String -> Parser String
symbol = L.symbol sc


rword :: String -> Parser String
rword word = do
  lexeme (string word *> notFollowedBy alphaNumChar)
  return word


rws :: [String] -- list of reserved words
rws = [
  "val",
  "var",
  "object",
  "class",
  "public",
  "protected",
  "private",
  "this",
  "mutable",
  "True",
  "new",
  "package",
  "println",
  "import",
  "implements",
  "extends",
  "let",
  "if",
  "then",
  "else",
  "while",
  "do",
  "skip",
  "True",
  "False",
  "not",
  "and",
  "or"
  ]


word :: Parser String
word = (lexeme . try) (p >>= check)
  where
    p       = (:) <$> alphaNumChar <*> many alphaNumChar
    check x = if x `elem` rws
                then fail $ "keyword " ++ show x ++ " cannot be an word"
                else return x

lexeme :: Parser a -> Parser a
lexeme = L.lexeme sc


integer :: Parser Integer
integer = lexeme L.decimal


parens :: Parser a -> Parser a
parens = between (symbol "(") (symbol ")")

identifier :: Parser String
identifier = (lexeme . try) (p >>= check)
  where
    p       = (:) <$> letterChar <*> many (alphaNumChar <|> char '_')
    check x = if x `elem` rws
                then fail $ "keyword " ++ show x ++ " cannot be an identifier"
                else return x