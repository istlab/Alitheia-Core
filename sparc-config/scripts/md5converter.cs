using System;
using System.Collections.Generic;
using System.Text;

namespace MD5Converter
{
	/// <summary>
	/// Quick and dirty converter of hex-encoded md5 passwords to Base64 format, it could
	/// probably be implemented in perl in a couple of lines
	/// </summary>
	class MD5Converter
	{
		static void Main(string[] args)
		{
			if (args.Length == 0)
			{
				Console.WriteLine("Usage: MD5Converter [md5hex]");
				return;
			}
			string input = args[0];
			if (input.Length != 32)
			{
				Console.WriteLine("Invalid input length.");
				return;
			}
			byte[] vector = new byte[32], result = new byte[16];

			for (int i = 0; i < 32; i+=2)
			{
				CharToByte(input, vector, i);
				CharToByte(input, vector, i+1);
				result[i/2] = (byte)((vector[i] << 4) | vector[i + 1]);
			}

			Console.WriteLine(Convert.ToBase64String(result));
			Console.Read();
		}

		private static void CharToByte(string input, byte[] vector, int pos)
		{
			vector[pos] = byte.Parse(input.Substring(pos, 1), System.Globalization.NumberStyles.HexNumber);
		}
	}
}
