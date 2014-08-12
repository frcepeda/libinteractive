package com.omegaup.libinteractive.target

import java.io.File
import java.util.Random

import com.omegaup.libinteractive.idl.IDL
import com.omegaup.libinteractive.idl.Interface

case class Options(
	idlFile: File = new File("."),
	outputDirectory: File = new File("."),
	seed: Long = System.currentTimeMillis,
	sequentialIds: Boolean = false,
	verbose: Boolean = false
)

case class OutputFile(filename: String, contents: String) {
	override def toString() = {
		s"""OutputFile("$filename", "${contents}")"""
	}
}

abstract class Target(idl: IDL, options: Options) {
	protected val rand = new Random(options.seed)
	protected val functionIds = idl.interfaces.flatMap (interface => {
		interface.functions.map(
			function => (idl.main.name, interface.name, function.name) -> nextId) ++
		idl.main.functions.map(
			function => (interface.name, idl.main.name, function.name) -> nextId)
	}).toMap

	private var currentId = 0
	private def nextId() = {
		if (options.sequentialIds) {
			currentId += 1
			currentId
		} else {
			rand.nextInt
		}
	}

	protected def pipeFilename(interface: Interface) = {
		interface.name match {
			case "Main" => "Main_pipes/out"
			case name: String => s"${name}_pipes/in"
		}
	}

	protected def pipeName(interface: Interface) = {
		interface.name match {
			case "Main" => "out"
			case name: String => s"${name}_in"
		}
	}

	def generateParent(): Iterable[OutputFile]
	def generateChildren(): Iterable[OutputFile]
}

/* vim: set noexpandtab: */
