package ru.mmb.terminal.util;

import java.io.File;

import android.os.Environment;

public class ExternalStorage
{
	public static File getDir()
	{
		return Environment.getExternalStorageDirectory();
	}
}
