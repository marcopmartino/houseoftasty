package it.project.houseoftasty.databaseInterface;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import it.project.houseoftasty.dataModel.Product;
import it.project.houseoftasty.dataModel.ProductId;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class HouseTastyDao_Impl implements HouseTastyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Product> __insertionAdapterOfProduct;

  private final EntityDeletionOrUpdateAdapter<Product> __deletionAdapterOfProduct;

  private final EntityDeletionOrUpdateAdapter<ProductId> __deletionAdapterOfProductIdAsProduct;

  private final EntityDeletionOrUpdateAdapter<Product> __updateAdapterOfProduct;

  public HouseTastyDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProduct = new EntityInsertionAdapter<Product>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `product` (`id`,`nome`,`quantita`,`unitaMisura`,`scadenza`) VALUES (?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Product value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getNome() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getNome());
        }
        if (value.getQuantita() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getQuantita());
        }
        if (value.getUnitaMisura() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getUnitaMisura());
        }
        if (value.getScadenza() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getScadenza());
        }
      }
    };
    this.__deletionAdapterOfProduct = new EntityDeletionOrUpdateAdapter<Product>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `product` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Product value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
      }
    };
    this.__deletionAdapterOfProductIdAsProduct = new EntityDeletionOrUpdateAdapter<ProductId>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `product` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ProductId value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
      }
    };
    this.__updateAdapterOfProduct = new EntityDeletionOrUpdateAdapter<Product>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `product` SET `id` = ?,`nome` = ?,`quantita` = ?,`unitaMisura` = ?,`scadenza` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Product value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getNome() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getNome());
        }
        if (value.getQuantita() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getQuantita());
        }
        if (value.getUnitaMisura() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getUnitaMisura());
        }
        if (value.getScadenza() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getScadenza());
        }
        if (value.getId() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getId());
        }
      }
    };
  }

  @Override
  public void insert(final Product... product) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfProduct.insert(product);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Product product) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfProduct.handle(product);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteById(final ProductId... id) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfProductIdAsProduct.handleMultiple(id);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Product product) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfProduct.handle(product);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Product[] getAll() {
    final String _sql = "SELECT * FROM product";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfNome = CursorUtil.getColumnIndexOrThrow(_cursor, "nome");
      final int _cursorIndexOfQuantita = CursorUtil.getColumnIndexOrThrow(_cursor, "quantita");
      final int _cursorIndexOfUnitaMisura = CursorUtil.getColumnIndexOrThrow(_cursor, "unitaMisura");
      final int _cursorIndexOfScadenza = CursorUtil.getColumnIndexOrThrow(_cursor, "scadenza");
      final Product[] _result = new Product[_cursor.getCount()];
      int _index = 0;
      while(_cursor.moveToNext()) {
        final Product _item;
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        final String _tmpNome;
        if (_cursor.isNull(_cursorIndexOfNome)) {
          _tmpNome = null;
        } else {
          _tmpNome = _cursor.getString(_cursorIndexOfNome);
        }
        final String _tmpQuantita;
        if (_cursor.isNull(_cursorIndexOfQuantita)) {
          _tmpQuantita = null;
        } else {
          _tmpQuantita = _cursor.getString(_cursorIndexOfQuantita);
        }
        final String _tmpUnitaMisura;
        if (_cursor.isNull(_cursorIndexOfUnitaMisura)) {
          _tmpUnitaMisura = null;
        } else {
          _tmpUnitaMisura = _cursor.getString(_cursorIndexOfUnitaMisura);
        }
        final String _tmpScadenza;
        if (_cursor.isNull(_cursorIndexOfScadenza)) {
          _tmpScadenza = null;
        } else {
          _tmpScadenza = _cursor.getString(_cursorIndexOfScadenza);
        }
        _item = new Product(_tmpId,_tmpNome,_tmpQuantita,_tmpUnitaMisura,_tmpScadenza);
        _result[_index] = _item;
        _index ++;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Product[] getById(final String id) {
    final String _sql = "SELECT * FROM product WHERE id= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfNome = CursorUtil.getColumnIndexOrThrow(_cursor, "nome");
      final int _cursorIndexOfQuantita = CursorUtil.getColumnIndexOrThrow(_cursor, "quantita");
      final int _cursorIndexOfUnitaMisura = CursorUtil.getColumnIndexOrThrow(_cursor, "unitaMisura");
      final int _cursorIndexOfScadenza = CursorUtil.getColumnIndexOrThrow(_cursor, "scadenza");
      final Product[] _result = new Product[_cursor.getCount()];
      int _index = 0;
      while(_cursor.moveToNext()) {
        final Product _item;
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        final String _tmpNome;
        if (_cursor.isNull(_cursorIndexOfNome)) {
          _tmpNome = null;
        } else {
          _tmpNome = _cursor.getString(_cursorIndexOfNome);
        }
        final String _tmpQuantita;
        if (_cursor.isNull(_cursorIndexOfQuantita)) {
          _tmpQuantita = null;
        } else {
          _tmpQuantita = _cursor.getString(_cursorIndexOfQuantita);
        }
        final String _tmpUnitaMisura;
        if (_cursor.isNull(_cursorIndexOfUnitaMisura)) {
          _tmpUnitaMisura = null;
        } else {
          _tmpUnitaMisura = _cursor.getString(_cursorIndexOfUnitaMisura);
        }
        final String _tmpScadenza;
        if (_cursor.isNull(_cursorIndexOfScadenza)) {
          _tmpScadenza = null;
        } else {
          _tmpScadenza = _cursor.getString(_cursorIndexOfScadenza);
        }
        _item = new Product(_tmpId,_tmpNome,_tmpQuantita,_tmpUnitaMisura,_tmpScadenza);
        _result[_index] = _item;
        _index ++;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
