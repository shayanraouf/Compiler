#! /usr/bin/python

"""
	This script generates text files to bused for testing 
	the robustness of the Lexical Analyzer

"""

import random
import sys

masterlist = ['&','&&','||','|','^','{','}','[',']','(',')', ' ',
			'+','-', '*', '/', '==', '!=', '>=', '<=', '<', '>' 
			'!', '~','>>', '<<', '=',
			'byte', 'const', 'else', 'end', 'exit', 'float64', 
			'for', 'function','if', 'int32', 'print', 'record', 
			'ref', 'return', 'static', 'type','var', 'while'
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
			'a','b','c','d','e','f','g','h','i','j', 'k', 'l'
			 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
			 'A','B','C','D','E','F','G','H','I','J', 'K', 'L'
			 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
			 'byte', 'const', 'else', 'end', 'exit', 'float64', 
			'for', 'function','if', 'int32', 'print', 'record', 
			'ref', 'return', 'static', 'type','var', 'while']



operators = ['&','&&','||','|','^','{','}','[',']','(',')',
			'+','-', '*', '/', '==', '!=', '>=', '<=', '<', '>' 
			'!', '~','>>', '<<', '=']

nums = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '0']

reserved = ['byte', 'const', 'else', 'end', 'exit', 'float64', 
			'for', 'function','if', 'int32', 'print', 'record', 
			'ref', 'return', 'static', 'type','var', 'while']

identifiers = ['abbazabba', 'orange3312jj3', 'shah', 'snoopdiz34le', 'd11111qas'
			   'JTsnazzypants24', 'fort', 'stati', 'ar', 'tye', 'jOn', 'yoyo'
			   'holla44magnum', 'seattle', 'seahawks', 'RGIII', 'Brady']

lowercase = ['a','b','c','d','e','f','g','h','i','j', 'k', 'l'
			 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']

uppercase = ['A','B','C','D','E','F','G','H','I','J', 'K', 'L'
			 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']

# This will produce 20 lines of random characters from the master list
for i in range(20):
    string = ''.join([random.choice(masterlist) for _ in range(int(30))])
    print string
