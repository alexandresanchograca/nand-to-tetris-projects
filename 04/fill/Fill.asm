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

(MAINLOOP)
@SCREEN
D=A 
@screenAddr
M=D 

@i 
M = 0

@8191
D=A
@counter
M=D


@24576
D=M
@FILLOOP
D,JNE
@WHITELOOP
D,JEQ
@MAINLOOP
0;JMP

(FILLOOP)
@screenAddr
A=M 
M = -1
@screenAddr
M = M + 1

@i
M = M + 1
D = M
@counter
D = M - D 

@FILLOOP
D, JGE

@MAINLOOP
0;JMP

(WHITELOOP)
@screenAddr
A=M 
M = 0
@screenAddr
M = M + 1

@i
M = M + 1
D = M
@counter
D = M - D 

@WHITELOOP
D, JGE

@MAINLOOP
0;JMP