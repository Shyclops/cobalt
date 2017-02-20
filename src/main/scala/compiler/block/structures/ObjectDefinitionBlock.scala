package compiler.block.structures

import compiler.block.Block
import compiler.block.imports.ImportBlock
import compiler.block.packages.PackageBlock
import compiler.block.structures.kinds.{ClassBlock, ObjectBlock}
import compiler.generators.structures.ObjectDefinitionGen
import compiler.structure.parameters.Parameter
import compiler.symbol_table.SymbolTable
import compiler.utilities.Utils


// Creation of a new object and storing to a variable
class ObjectDefinitionBlock(superBlockInit: Block, declaration : Boolean, className: String, variableName: String, operator: String, newKeyword: String, initClassName: String, params: Array[Parameter]) extends Block(superBlockInit, false, true ,false) {

  private var parameterString: String = ""
  private var argumentString: String = ""
  private var directory: String = ""

  def init() {
    if (className == getObjectName) {
      directory = Utils.getPackage(this)
    }
    else {
      directory = Utils.getDirectory(this, className)
    }

    // Get the type of the parameters
    for (param <- params) {
      param.setType(SymbolTable.getInstance.getValue(Utils.getMethod(this), param.getName).getType)
      parameterString += param.getAsmType
      argumentString += "mv.visitIntInsn(ILOAD, " + SymbolTable.getInstance.getValue(Utils.getMethod(this), param.getName).getId + ");"
    }
  }





  // Returns the main class name for the file
  def getObjectName: String = {
    // Get the FileBlock to find the imports
    var block: Block = this
    while (!(block.isInstanceOf[ClassBlock]) && !(block.isInstanceOf[ObjectBlock])) {
        block = block.superBlock
    }
    // Get the directory of the Object
    return block.getName
  }

  def getName: String = variableName

  def getValue: String = ""

  def getType: String = className

  def getOpeningCode: String = {
    return "mv.visitTypeInsn(NEW, \"" + directory + (if (directory == "") ""
    else "/") + className + "\");\n" + "mv.visitInsn(DUP);\n" + argumentString + "mv.visitMethodInsn(INVOKESPECIAL, \"" + directory + (if (directory == "") ""
    else "/") + className + "\", \"<init>\", \"(" + parameterString + ")V\", false);\n" + "mv.visitVarInsn(ASTORE," + id + ");\n"
  }

  def getClosingCode: String = {
    return ""
  }

  override def toString: String = "object: " + className + " " + variableName + " = new " + initClassName + "()"

}