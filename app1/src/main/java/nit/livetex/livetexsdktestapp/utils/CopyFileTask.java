package nit.livetex.livetexsdktestapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

final class CopyFileTask {

	private final Uri uri;
	private InputStream is = null;

	CopyFileTask(Uri uri) {
		this.uri = uri;
	}

	protected String run(Context context) {
		File file = null;
		int size = -1;

		File folder = context.getExternalFilesDir("Temp");
		Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
		try {
			is = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			try {
				if (returnCursor != null && returnCursor.moveToFirst()) {
					if (uri.getScheme() != null) {
						if (uri.getScheme().equals("content")) {
							int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
							size = (int) returnCursor.getLong(sizeIndex);
						} else if (uri.getScheme().equals("file")) {
							File ff = new File(uri.getPath());
							size = (int) ff.length();
						}
					}
				}
			} finally {
				if (returnCursor != null) {
					returnCursor.close();
				}
			}

			file = new File(folder + "/" + getFileName(uri, context));

			BufferedInputStream bis = new BufferedInputStream(is);
			FileOutputStream fos = new FileOutputStream(file);

			byte[] data = new byte[1024];
			long total = 0;
			int count;
			while ((count = bis.read(data)) != -1) {
				total += count;
				fos.write(data, 0, count);
			}
			fos.flush();
			fos.close();
		} catch (IOException e) {
			Log.e("IOException = ", e.getMessage());
		}

		return file.getAbsolutePath();
	}

	private String getFileName(Uri uri, Context context) {
		String result = null;
		if (uri.getScheme() != null) {
			if (uri.getScheme().equals("content")) {
				Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				}
				if (cursor != null) {
					cursor.close();
				}
			}
		}
		if (result == null) {
			result = uri.getPath();
			assert result != null;
			int cut = result.lastIndexOf('/');
			if (cut != -1) {
				result = result.substring(cut + 1);
			}
		}
		return result;
	}
}
