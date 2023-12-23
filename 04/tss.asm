// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

@SCREEN // it automatically gets the screen memory map
D=A 
@addr
M=D  //assign the screen memory map adress to a variable, this is used because we increment this value to draw

@0  
D=M
@n 
M=D  // n = *0 que é o numero de colunas


@i  //18

(LOOP)
@i
D=M
@n 
D=D-M 

@END
D;JGT 

@addr 
A=M 
M=-1  //Draw a black line at the screen memory map adress

@i 
M=M+1
@32   //i = 32 * row + col/16 ou RAM[16384 + 32 * row + col/16] -- 256 x 512 pixeis cada pixel é um bit - tamanho na RAM é 8191
D=A 
@addr 
M=D+M

@LOOP
0;JMP

(END)
@END
0;JMP