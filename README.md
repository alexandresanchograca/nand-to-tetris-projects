# nand-to-tetris-projects

All my nand to tetris project solutions.

The overall course objective is to make a computer from scratch that was able to run tetris.
https://www.nand2tetris.org/

It contains all the logic for a simple and usable computer able to run tetris, pong or other games.

I took the course on cousera, mainly by following the videos while also reading the book The Elements of Computing Systems.
It's a two part course.
The first part we were given a NAND gate and made all the other logic gates with a simplified hardware description language (HDL) using boolean algebra.
With this gates we made a ALU that made all the basic arithmetic operations. Adding, and, or, not bitwise operations, and subtraction was inherent due to the 2's complement representation. 
Ex: x + (-y).
Then it was given to us a data flip flop logic gate, with this gate we made 16 bit memory registers to make the RAM module. The CPU was built using the ALU we built previously and the 16 bit memory registers.
To be easier to give instructions to our CPU we made an assembler. The assembly language specification was provided by the course. This now provided us an abstraction layer to work with our CPU. And it was much easier compared to giving the CPU 16 bit binary instructions.

For the second part we resumed after the built assembler and made a VM translator, in this module we were introduced to virtual memory segments such as static, local, arguments, this, that and temp memory segments. Also introduced the concept of the working stack and function call stack. This VM translator would translate VM code to assembly, and would provide us another layer of abstraction to give instructions to our CPU.
After this we continued to develop another major abstraction, a high level object oriented language, the language specification was given in the course and we made a compiler for it. Now we can give instructions to our computer using a high level language! This was awesome, although the language is pretty crude to today's modern industrial strength programming languages.
Finally we reached the end of the course by making a "simple" Operating System. This OS was made so our application programmers could make well... applications. We built numerous OS classes such as Sys, Math, Screen, Output... so that our application programmers can use these classes and not bother with low level details of the hardware platform. The OS is initialized with the Sys class that initializes our core OS classes and then executes the program that the application programer wrote that must be specified as a Main named class, and the entry point must be a function that it's identifier is also name.

This is a overall course summary, of course you can find more info on coursera, the book or the course website.
Highly recommend the course. The first part is pretty acessible to all skill levels. I found the second part much, much more demanding.

The code presented here is not great, or even good, but it was frustrating and fun to write.
