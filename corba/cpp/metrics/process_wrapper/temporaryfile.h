#ifndef TEMPORARYFILE_H
#define TEMPORARYFILE_H

#include <string>
#include <iostream>

/**
 * The TemporaryDirectory class is providing a temporary directory in the file system.
 *
 * TemporaryDirectory creates a unique temporary directory safely. The directory is deleted 
 * upon destruction of the object.
 */
class TemporaryDirectory
{
public:
    explicit TemporaryDirectory( const char* templateName );
    ~TemporaryDirectory();

    std::string name() const;

private:
    TemporaryDirectory( const TemporaryDirectory& ) {}
    char* m_name;
};

/**
 * The TemporaryFile class is an I/O stream that operates on a temporary file.
 *
 * TemporaryFile creates a unique temporary file safely. The file is deleted upon
 * destruction of the object.
 */
class TemporaryFile : public std::iostream
{
public:
    explicit TemporaryFile( const char* templateName, std::ios_base::openmode mode = std::ios_base::in | std::ios_base::out );
    ~TemporaryFile();

    std::string name() const;
    void close();

private:
    TemporaryFile( const TemporaryFile& ) {}
    char* m_name;
};

#endif
