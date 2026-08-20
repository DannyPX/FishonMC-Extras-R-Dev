package dannypx.foe.placeholder.parser;

import dannypx.foe.placeholder.lexing.PlaceholderBalanceHelper;
import dannypx.foe.placeholder.lexing.PlaceholderTokenizer;
import dannypx.foe.placeholder.parser.ast.*;
import dannypx.foe.placeholder.registry.PlaceholderRegistry;
import dannypx.foe.placeholder.registry.PlaceholderTreeNode;
import dannypx.foe.placeholder.token.PlaceholderParseException;
import dannypx.foe.placeholder.token.Token;
import dannypx.foe.placeholder.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderParser {
    private final String source;
    private final List<Token> tokens;
    private int pos = 0;

    public PlaceholderParser(String source) {
        PlaceholderBalanceHelper.checkBalanced(source);
        this.source = source;
        this.tokens = PlaceholderTokenizer.of(source).tokenize();
    }

    /// Top

    public Group parse() {
        List<Node> children = new ArrayList<>();

        while(tokens.get(pos).type() != TokenType.EOF) {
            Token token = tokens.get(pos);

            if(token.type() == TokenType.PERCENT) {
                children.add(this.parsePlaceholderTop());
            } else {
                pos++;
                children.add(new Literal(token.text()));
            }
        }
        return new Group(children);
    }

    private Node parsePlaceholderTop() {
        this.expect(TokenType.PERCENT);

        if(this.peek().type() == TokenType.PERCENT) {
            this.advance();
            return new Literal("");
        }

        Node body = this.parsePlaceholderBody();
        this.expect(TokenType.PERCENT);
        return body;
    }

    private Node parseNestedPlaceholder() {
        this.expect(TokenType.LT);
        Node body = this.parsePlaceholderBody();
        this.expect(TokenType.GT);
        return body;
    }

    /// Body

    private Node parsePlaceholderBody() {
        Token firstToken = this.expectPathSegment();
        String first = firstToken.text();
        PlaceholderTreeNode current = PlaceholderRegistry.getRoot(first);

        int errorStart = firstToken.start();
        int errorEnd = firstToken.end();
        String offendingSegment = first;
        boolean failed = current == null;

        List<String> capturedIndices = new ArrayList<>();

        while(this.peek().type() == TokenType.DOT) {
            this.advance();
            if(this.peek().type() == TokenType.LPARENTHESIS) {
                break;
            }

            Token segmentToken = this.expectPathSegment();
            String segment = segmentToken.text();
            errorEnd = segmentToken.end();

            if(!failed) {
                PlaceholderTreeNode child = current.resolveChild(segment, capturedIndices);

                if(child == null) {
                    failed = true;
                    offendingSegment = segment;
                }

                current = child;
            }
        }

        String fullPath = source.substring(errorStart, errorEnd);

        if(this.peek().type() == TokenType.LPARENTHESIS) {
            this.advance();
            List<Node> args = new ArrayList<>();

            if(this.peek().type() != TokenType.RPARENTHESIS) {
                args.add(this.parseArgument());
                while(this.peek().type() == TokenType.COMMA) {
                    this.advance();
                    args.add(this.parseArgument());
                }
            }

            this.expect(TokenType.RPARENTHESIS);

            if(failed || current == null || !current.hasEval()) {
                return error(fullPath, offendingSegment, errorStart, errorEnd);
            }

            return new FunctionCall(current, args);
        }

        if(failed || current == null || !current.hasResolver()) {
            return error(fullPath, offendingSegment, errorStart, errorEnd);
        }

        return new PlaceholderReference(current, capturedIndices);
    }

    private AstError error(String fullPath, String offendingSegment, int start, int end) {
        String message = "Unresolved placeholder '" + fullPath + "' (unknown segment: '" + offendingSegment + "')";
        return new AstError(message, start, end);
    }

    private Token expectPathSegment() {
        Token token = this.peek();
        if(token.type() == TokenType.IDENTIFIER || token.type() == TokenType.NUMBER) {
            return this.advance();
        }
        throw new PlaceholderParseException(
                "Expected path segment at position " + token.start() + ", got " + token.type(), token.start()
        );
    }

    /// Arguments / Expressions

    private Node parseArgument() {
        return this.parseExpression();
    }

    private Node parseExpression() {
        Node left = parseOperand();
        TokenType type = this.peek().type();

        if(this.isComparisonOp(type) || this.isArithmeticOp(type)) {
            String op = this.advance().text();
            Node right = this.parseOperand();
            return new BinaryOp(op, left, right);
        }

        return left;
    }

    private Node parseOperand() {
        TokenType type = this.peek().type();

        switch (type) {
            case MINUS -> {
                this.advance();
                Node operand = this.parseOperand();

                return new UnaryOp("-", operand);
            }
            case LT -> {
                return this.parseNestedPlaceholder();
            }
            case STRING, NUMBER, IDENTIFIER -> {
                Token token = this.advance();
                return new Literal(token.text());
            }
            default -> {
                Token unexpected = this.peek();
                throw new PlaceholderParseException(
                        "Expected an operand at position " + unexpected.start() + ", got " + type, unexpected.start()
                );
            }
        }
    }

    private boolean isComparisonOp(TokenType type) {
        return type == TokenType.LT
                || type == TokenType.GT
                || type == TokenType.LTE
                || type == TokenType.GTE
                || type == TokenType.EQ
                || type == TokenType.NOT_EQ;
    }

    private boolean isArithmeticOp(TokenType type) {
        return type == TokenType.PLUS
                || type == TokenType.MINUS
                || type == TokenType.STAR
                || type == TokenType.SLASH;
    }

    /// Helpers

    private void skipWhitespaceTokens() {
        while(tokens.get(pos).type() == TokenType.WHITESPACE && pos < tokens.size() - 1) {
            pos++;
        }
    }

    private Token peek() {
        this.skipWhitespaceTokens();
        return tokens.get(pos);
    }

    private Token advance() {
        this.skipWhitespaceTokens();
        Token token = tokens.get(pos);
        if(pos < tokens.size() - 1) pos ++;
        return token;
    }

    private Token expect(TokenType type) {
        Token token = this.peek();
        if(token.type() != type) {
            throw new PlaceholderParseException(
                    "Expected " + type + " at position " + token.start() + ", got " + token.type() + " ('" + token.text() + "')", token.start()
            );
        }
        return this.advance();
    }
}
