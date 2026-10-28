using System;
using MyApp.Core;

namespace MyApp.Console
{
    internal static class Program
    {
        private static int Main(string[] args)
        {
            var calc = new Calculator();
            Console.WriteLine($"2 + 3 = {calc.Add(2, 3)}");
            Console.WriteLine($"10 / 4 = {calc.Divide(10, 4)}");
            return 0;
        }
    }
}