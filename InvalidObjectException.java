/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package termproject;

/**
 * @author micahvranyes
 * Description: Provides an exception type for when inputted
 * objects are invalid.
 */
public class InvalidObjectException extends RuntimeException {
    public InvalidObjectException() {
        super ("Problem with TFNode");
    }
    public InvalidObjectException(String errorMsg) {
        super (errorMsg);
    }
}
