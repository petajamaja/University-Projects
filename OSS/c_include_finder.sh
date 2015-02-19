#! /usr/bin/env bash
#=================================================================================================
#
#         FILE:    c_include_finder.sh
#
#        USAGE:    c_include_finder.sh [-h | --help] | [ ['paths'][C/C++ source filenames] ]
#
#  DESCRIPTION:    Script for finding and checking the existence of C/C++ include files.
#
#      OPTIONS:    ---
# REQUIREMENTS:    ---
#         BUGS:    Countless.
#        NOTES:    ---
#       AUTHOR:    Natalia Bogutskaya, 2.year bachelor student, bogutnat@fel.cvut.cz
#      COMPANY:    CTU Prague, Faculty of Electrical Engeneering
#      VERSION:    8.0
#      CREATED:    09.11.2012
#     REVISION:    ---
#==================================================================================================

ERR_WRONG_PARAM=1             # wrong type of parameter or no parameters at all 
ERR_NO_SOURCEFILE=2           # the sourcefile is not specified or doesn't exist 
ERR_FILE_NOT_FOUND=3          # include file has not been found in any of the paths
ERR_WRONG_PARAM_ORDER=4       # occurs if a path appears between filenames


EXIT_SUCCESSFUL=0

TRUE=1
FALSE=0

function display_help_information(){
     echo ""
     echo "USAGE: c_include_finder.sh [-h ] [ --help ]  [[ paths to include files in single quotes ][ C/C++ sourcefiles in single quotes]]"
     exit $EXIT_SUCCESSFUL
}

#===  FUNCTION  =========================================================================
#         NAME:  is_input_correct
#  DESCRIPTION:  Checks if a parameter is valid (file, directory, or request for help)
# PARAMETER  1:  One of the command line parameters.
#=========================================================================================
function is_input_correct(){

     param=$1
     local is_correct
     if  [ ! -f "${param}" ] && [ ! -d "${param}" ] && [ ! "${param}" = "-h" ] && [ ! "${param}" = "--help" ]; then
          is_correct="$FALSE"
     else
          is_correct="$TRUE"
     fi 
     echo "${is_correct}"
}

#---------------------------------------------------------------------------------------------------------
# Cecking if the parameters are correct by looping through them.
# This is essential because there is no sense in continuing if any
# parameter doesn't match. Also checks if there are no parameters specified. 
#---------------------------------------------------------------------------------------------------------

no_source_file=$TRUE

echo ""
if [ "$#" == "0" ]; then
    echo "ERROR: No arguments provided!">&2
    exit $ERR_WRONG_PARAM
fi

for param in "$@"
    do
      correct=`is_input_correct "${param}"`
      if  [ "${correct}" = $FALSE ]; then

         echo "ERROR: Wrong parameter type : $param ">&2
         exit $ERR_WRONG_PARAM

      else
         if  [ "${param}" = "-h" ]||[ "${param}" = "--help" ]; then
            display_help_information
            no_source_file=$FALSE
            break
         fi

         if [ -f "${param}" ] ; then
            no_source_file=$FALSE
         fi
      fi

      if [ "$no_source_file" = $FALSE ] && [ -d "${param}" ] ; then
         echo "ERROR: Wrong parameter order: path to include file between source file names">&2
         exit $ERR_WRONG_PARAM_ORDER
      fi
    done


if [ "${no_source_file}" = $TRUE ]; then
   echo " ERROR: No C/C++ source file specified! ">&2
   exit $ERR_NO_SOURCEFILE
fi

#-------------------------------------------------------------------------------------------------------------
# Creating an array with paths. Needed to be done because it is impractical to loop through cmd line each time
#-------------------------------------------------------------------------------------------------------------
i=0
for param in "$@"
    do
      if [ -d "${param}" ]; then
          array_with_paths[i]="${param}"
          i=$((i + 1))
      fi
    done

#===  FUNCTION  =====================================================================
#         NAME:  find_include
#  DESCRIPTION:  Checks if a sourcefile is in any of the specified directories
# PARAMETER  1:  Name of the file we are looking for
# PARAMETER  2:  Directory where the c/c++ sourcefile is situated
#=====================================================================================

function find_include(){

    include_filename=$1
    c_file_dir=$2
    found=0

    # first,looks in the same directory where the source file is
   
    if [  -f "${c_file_dir}"/"${include_filename}" ]; then
       found=1
       echo "${c_file_dir}"/"${include_filename}" found!

    # then,browses through listed directories from pre-created array 
    else
       for path_item in "${array_with_paths[@]}"
           do
           if [ -f "${path_item}"/"${include_filename}" ]; then
             found=1
             echo "${path_item}"/"${include_filename}" found!
             break
           fi
           done
    fi
 
    if [ "${found}" = 0  ]; then
       echo "ERROR: "$include_filename "file not found in any of listed directories">&2
    fi
    return 0
   
}

#-------------------------------------------------------------------------------------------------------------
# Main part of the code - actual search, call of find_include function
#-------------------------------------------------------------------------------------------------------------


for param in "$@"
    do
      if [ -f "${param}" ] ; then

         filename=$( basename "${param}" )
         dirname=$( dirname "${param}" )

         echo "INCLUDE FILES FOR SOURCE FILE" "${param}" ":"

         # searching for the #include  makros
         cat $param | grep -E '(\s*\#\s*include[[:space:]]*<([^<>]+)>)|((\s)*\#(\s)*include[[:space:]]*\"([^\"]+)\")' |

         # removing all unnesessary characters from each line.
         # first, removes <,>,#," characters;
         # then, removes the include word;
         # last, removes all the leading and final spaces, leaving those in the middle untouched;
         while read line ; do
             result=`echo "$line" | tr -d '<>#\"' | sed 's/.*include\(.*\)$/\1/' | sed 's/^ *//;s/ *$//'`
             find_include "${result}" "${dirname}"
             
         done
         echo""
      fi
    done
  
         


         
     
