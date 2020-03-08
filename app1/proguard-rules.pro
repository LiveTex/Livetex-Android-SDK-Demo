-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
-ignorewarnings

# parcelable
-keepclassmembers enum * {
	public static **[] values();
	public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
	public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
	private static final long serialVersionUID;
	private static final java.io.ObjectStreamField[] serialPersistentFields;
	!static !transient <fields>;
	private void writeObject(java.io.ObjectOutputStream);
	private void readObject(java.io.ObjectInputStream);
	java.lang.Object writeReplace();
	java.lang.Object readResolve();
}

# keep everything in this package from being removed or renamed
-keep class nit.livetex.** { *; }

# keep everything in this package from being renamed only
-keepnames class nit.livetex.** { *; }