package com.quickhomework.Classes;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//класс, предназначенный для взаимодействия с внутренней базой данных приложения
public class Repository {

	// константы названий в БД
	final String DATA_BASE_NAME = "QuickhomeworK";
	final String TABLE = "Subjects";
	final String COLUMN_ID = "ID";
	final String COLUMN_NAME = "name";
	final String COLUMN_ISLOCKED = "idlock";
	final String COLUMN_COUNT_OF_HOME_WORK = "count";
	final String COLUMN_HOME_WORK_TEXT = "text_";

	// экземпляры для управления БД из всего класса
	DBHelper dbHelper;
	SQLiteDatabase db;



	// в конструкторе создаем экземпляры DBHelper и db
	public Repository(Context context) {
		dbHelper = new DBHelper(context);
		db = dbHelper.getWritableDatabase();
	}



	// добавление нового предмета в таблицу БД
	public void AddSubject(String name) {
		ContentValues cv = new ContentValues();

		// вычисляем новый ID
		ArrayList<Subject> subjects = this.GetSubjects();
		int maxID = 0;
		for (Subject subject : subjects) {
			if (subject.id > maxID)
				maxID = subject.id;
		}
		maxID++;

		// добавляем все поля
		cv.put(COLUMN_ID, maxID);
		cv.put(COLUMN_NAME, name);
		cv.put(COLUMN_ISLOCKED, false);
		cv.put(COLUMN_COUNT_OF_HOME_WORK, 0);

		db.insert(TABLE, null, cv);
	}



	// получить предмет по id
	public Subject GetSubject(int id) {
		// делаем запрос с условием
		Cursor c = db.query(TABLE, null, COLUMN_ID + " = " + id, null, null,
				null, null);
		return this.GetSubjectsFromCursor(c).get(0);
	}



	// достает из БД весь список
	public ArrayList<Subject> GetSubjects() {
		// делаем запрос всех данных из таблицы , получаем Cursor

		String orderBy = COLUMN_ISLOCKED + " DESC, " + COLUMN_NAME + " ASC";
		Cursor c = db.query(TABLE, null, null, null, null, null, orderBy);

		return GetSubjectsFromCursor(c);

	}



	public void PhotoWasAdded(int id) {
		ContentValues cv = new ContentValues();
		;
		Subject subject = this.GetSubject(id);
		if (subject.countOfHomeWork < Subject.MAX_COUNT_OF_HOME_WORKS) {
			// еще не все были добавлены
			cv.put(COLUMN_COUNT_OF_HOME_WORK, subject.countOfHomeWork + 1);
		}
		db.update(TABLE, cv, "id = ?", new String[] { String.valueOf(id) });
	}



	// удаляет предмет
	public void DeleteSubject(int id) {
		db.delete(TABLE, "id = ?", new String[] { String.valueOf(id) });
	}



	public void DeleteAllSubjects() {
		db.delete(TABLE, null, null);
	}



	public void SetLocking(int id, boolean isLocked) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_ISLOCKED, isLocked);
		db.update(TABLE, cv, "id = ?", new String[] { String.valueOf(id) });
	}



	public void RenameSubject(int id, String name) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME, name);
		db.update(TABLE, cv, "id = ?", new String[] { String.valueOf(id) });
	}



	public void SaveNewTextHomeWork(int id, String text) {
		ContentValues cv;
		Subject subject = this.GetSubject(id);
		if (subject.countOfHomeWork < Subject.MAX_COUNT_OF_HOME_WORKS) {
			// еще не все были добавлены
			cv = new ContentValues();
			cv.put(COLUMN_HOME_WORK_TEXT
					+ String.valueOf(subject.countOfHomeWork), text);
			cv.put(COLUMN_COUNT_OF_HOME_WORK, subject.countOfHomeWork + 1);
		} else {
			// места нет, надо смещать
			cv = new ContentValues();
			// смещаем
			for (int i = 0; i < Subject.MAX_COUNT_OF_HOME_WORKS - 1; i++) {
				cv.put(COLUMN_HOME_WORK_TEXT + String.valueOf(i),
						subject.textOfHomeWork.get(i + 1));
			}
			cv.put(COLUMN_HOME_WORK_TEXT
					+ String.valueOf(Subject.MAX_COUNT_OF_HOME_WORKS - 1), text);
		}
		db.update(TABLE, cv, "id = ?", new String[] { String.valueOf(id) });
	}



	public void SaveTextHomeWork(int id, String text) {
		ContentValues cv;
		Subject subject = this.GetSubject(id);
		cv = new ContentValues();
		cv.put(COLUMN_HOME_WORK_TEXT
				+ String.valueOf(subject.countOfHomeWork - 1), text);

		db.update(TABLE, cv, "id = ?", new String[] { String.valueOf(id) });
	}



	// получая курсор, преобразует его в список
	private ArrayList<Subject> GetSubjectsFromCursor(Cursor c) {
		ArrayList<Subject> subjects = new ArrayList<Subject>();

		// ставим позицию курсора на первую строку выборки
		// если в выборке нет строк, вернется false
		if (c.moveToFirst()) {

			do {
				// получаем значения по номерам столбцов, формируем и суем
				// в список

				int id = c.getInt(c.getColumnIndex(COLUMN_ID));
				String name = c.getString(c.getColumnIndex(COLUMN_NAME));
				boolean isLocked = (c.getInt(c.getColumnIndex(COLUMN_ISLOCKED)) == 1);
				int countOfHomeWorks = c.getInt(c
						.getColumnIndex(COLUMN_COUNT_OF_HOME_WORK));
				ArrayList<String> textHomeWork = new ArrayList<String>();
				for (int i = 0; i < Subject.MAX_COUNT_OF_HOME_WORKS; i++) {
					textHomeWork.add(c.getString(c
							.getColumnIndex(COLUMN_HOME_WORK_TEXT
									+ String.valueOf(i))));
				}

				Subject subject = new Subject(name, id, isLocked,
						countOfHomeWorks, textHomeWork);

				subjects.add(subject);
				// переход на следующую строку
				// а если следующей нет (текущая - последняя), то false -
				// выходим из цикла
			} while (c.moveToNext());

			c.close();

		}
		return subjects;
	}




	// *******************************************************
	// ВЛОЖЕННЫЙ КЛАСС////////////////////////////////////////////

	// класс для управления БД
	class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			// конструктор суперкласса
			super(context, DATA_BASE_NAME, null, 1);
		}



		@Override
		public void onCreate(SQLiteDatabase db) {

			// создаем таблицу с полями

			String sql = "create table " + TABLE + " ( " + COLUMN_ID
					+ " integer primary key, " + COLUMN_NAME + " text, "
					+ COLUMN_ISLOCKED + " boolean, ";

			for (int i = 0; i < Subject.MAX_COUNT_OF_HOME_WORKS; i++) {
				sql = sql + COLUMN_HOME_WORK_TEXT + String.valueOf(i)
						+ " integer,";
			}

			sql = sql + COLUMN_COUNT_OF_HOME_WORK + " integer );";

			db.execSQL(sql);

		}



		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

}
