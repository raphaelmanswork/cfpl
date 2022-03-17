package cfpl.generated;

import cfpl.Token;
import cfpl.enums.DataType;

import java.util.List;

public abstract class Stmt {
   public interface Visitor<R> {
    R visitInputStmt(Input stmt);
    R visitExecutableStmt(Executable stmt);
    R visitBlockStmt(Block stmt);
    R visitExpressionStmt(Expression stmt);
    R visitIfStmt(If stmt);
    R visitPrintStmt(Print stmt);
    R visitVarStmt(Var stmt);
  }
  public static class Input extends Stmt {
    public Input(List<Token> tokens) {
      this.tokens = tokens;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitInputStmt(this);
    }

    public final List<Token> tokens;
  }
  public static class Executable extends Stmt {
    public Executable(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExecutableStmt(this);
    }

    public final List<Stmt> statements;
  }
  public static class Block extends Stmt {
    public Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    public final List<Stmt> statements;
  }
  public static class Expression extends Stmt {
    public Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    public final Expr expression;
  }
  public static class If extends Stmt {
    public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    public final Expr condition;
    public final Stmt thenBranch;
    public final Stmt elseBranch;
  }
  public static class Print extends Stmt {
    public Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    public final Expr expression;
  }
  public static class Var extends Stmt {
    public Var(Token name, Expr initializer, DataType dataType) {
      this.name = name;
      this.initializer = initializer;
      this.dataType = dataType;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    public final Token name;
    public final Expr initializer;
    public final DataType dataType;
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
