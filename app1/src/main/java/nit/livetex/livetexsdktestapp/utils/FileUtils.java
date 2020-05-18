package nit.livetex.livetexsdktestapp.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nit.livetex.livetexsdktestapp.BuildConfig;

public final class FileUtils {

	public static Uri getOutputMediaFile(Context context) {
		File out = createOutputMediaFile(context);
		if (out == null)
			return null;
		return FileProvider.getUriForFile(
				context,
				BuildConfig.APPLICATION_ID + ".provider",
				out);
	}

	/**
	 * Create a File for saving an image or video
	 */
	private static File createOutputMediaFile(Context context) {
		File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
				"Cache");
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
				"IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}

	public static String getPath(Context context, Uri uri) {
		String returnedPath;

		returnedPath = getRealPathFromUri(context, uri);

		//Get the file extension
		final MimeTypeMap mime = MimeTypeMap.getSingleton();
		String subStringExtension = String.valueOf(returnedPath).substring(String.valueOf(returnedPath).lastIndexOf(".") + 1);
		String extensionFromMime = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));

		// Path is null
		if (returnedPath == null || returnedPath.equals("")) {
			// This can be caused by two situations
			// 1. The file was selected from a third party app and the data column returned null (for example EZ File Explorer)
			// Some file providers (like EZ File Explorer) will return a URI as shown below:
			// content://es.fileexplorer.filebrowser.ezfilemanager.externalstorage.documents/document/primary%3AFolderName%2FNameOfFile.mp4
			// When you try to read the _data column, it will return null, without trowing an exception
			// In this case the file need to copied/created a new file in the temporary folder
			// 2. There was an error
			// In this case call PickiTonCompleteListener and get/provide the reason why it failed

			if (uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
				return copyFile(uri, context);
			}
		}
		// Path is not null
		else {
			// This can be caused by two situations
			// 1. The file was selected from an unknown provider (for example a file that was downloaded from a third party app)
			// 2. getExtensionFromMimeType returned an unknown mime type for example "audio/mp4"
			//
			// When this is case we will copy/write the file to the temp folder, same as when a file is selected from Google Drive etc.
			// We provide a name by getting the text after the last "/"
			// Remember if the extension can't be found, it will not be added, but you will still be able to use the file

			if (!subStringExtension.equals(extensionFromMime) && uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
				return copyFile(uri, context);
			}
		}
		return returnedPath;
	}

	private static String copyFile(Uri uri, Context context) {
		return new CopyFileTask(uri).run(context);
	}

	private static String getRealPathFromUri(final Context context, final Uri uri) {
		if (DocumentsContract.isDocumentUri(context, uri)) {
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					if (split.length > 1) {
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					} else {
						return Environment.getExternalStorageDirectory() + "/";
					}
				} else {
					return "storage" + "/" + docId.replace(":", "/");
				}

			} else if (isRawDownloadsDocument(uri)) {
				String fileName = getFilePath(context, uri);
				String subFolderName = getSubFolders(uri);

				if (fileName != null) {
					return Environment.getExternalStorageDirectory().toString() + "/Download/" + subFolderName + fileName;
				}
				String id = DocumentsContract.getDocumentId(uri);

				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isDownloadsDocument(uri)) {
				String fileName = getFilePath(context, uri);

				if (fileName != null) {
					return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
				}
				String id = DocumentsContract.getDocumentId(uri);
				if (id.startsWith("raw:")) {
					id = id.replaceFirst("raw:", "");
					File file = new File(id);
					if (file.exists()) {
						return id;
					}
				}
				if (id.startsWith("raw%3A%2F")) {
					id = id.replaceFirst("raw%3A%2F", "");
					File file = new File(id);
					if (file.exists()) {
						return id;
					}
				}
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) {
			if (isGooglePhotosUri(uri)) {
				return uri.getLastPathSegment();
			}
			if (getDataColumn(context, uri, null, null) == null) {
				// some error
			}
			return getDataColumn(context, uri, null, null);
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	private static String getSubFolders(Uri uri) {
		String replaceChars = String.valueOf(uri).replace("%2F", "/").replace("%20", " ").replace("%3A", ":");
		String[] bits = replaceChars.split("/");
		String sub5 = bits[bits.length - 2];
		String sub4 = bits[bits.length - 3];
		String sub3 = bits[bits.length - 4];
		String sub2 = bits[bits.length - 5];
		String sub1 = bits[bits.length - 6];
		if (sub1.equals("Download")) {
			return sub2 + "/" + sub3 + "/" + sub4 + "/" + sub5 + "/";
		} else if (sub2.equals("Download")) {
			return sub3 + "/" + sub4 + "/" + sub5 + "/";
		} else if (sub3.equals("Download")) {
			return sub4 + "/" + sub5 + "/";
		} else if (sub4.equals("Download")) {
			return sub5 + "/";
		} else {
			return "";
		}
	}

	private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} catch (Exception e) {
			Log.e("FileUtils", "", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	private static String getFilePath(Context context, Uri uri) {
		Cursor cursor = null;
		final String[] projection = { MediaStore.Files.FileColumns.DISPLAY_NAME };
		try {
			cursor = context.getContentResolver().query(uri, projection, null, null,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
				return cursor.getString(index);
			}
		} catch (Exception e) {
			Log.e("FileUtils", "", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	private static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	private static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	private static boolean isRawDownloadsDocument(Uri uri) {
		String uriToString = String.valueOf(uri);
		return uriToString.contains("com.android.providers.downloads.documents/document/raw");
	}

	private static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	private static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}