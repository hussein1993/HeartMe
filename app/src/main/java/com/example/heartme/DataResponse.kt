package com.example.heartme

class DataResponse(name:String , threshold : Int) {
    var name:String? = null
    var threshold : Int? = null

    init {
        this.name = name
        this.threshold = threshold
    }

    public fun isSimilarTest(test : String):Int{
        var count =0
        var words = test.split(" ",",",".","-","_")
        for ( word in words){
            if(name?.lowercase()?.contains(word.lowercase()) == true){
                count++
            }
        }
        return count
    }
}