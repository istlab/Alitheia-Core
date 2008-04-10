//
// Boost.Process
//
// Copyright (c) 2006 Julio M. Merino Vidal.
//
// Distributed under the Boost Software License, Version 1.0.
// (See accompanying file LICENSE_1_0.txt or copy at
// http://www.boost.org/LICENSE_1_0.txt.)
//

//!
//! \file boost/process/detail/environment.hpp
//!
//! Includes the declaration of the environment class.  This file is for
//! internal usage only and must not be included by the library user.
//!

#if !defined(BOOST_PROCESS_DETAIL_ENVIRONMENT_HPP)
/** \cond */
#define BOOST_PROCESS_DETAIL_ENVIRONMENT_HPP
/** \endcond */

#include <boost/process/config.hpp>

#if defined(BOOST_PROCESS_POSIX_API)
#   include <cstring>
#elif defined(BOOST_PROCESS_WIN32_API)
extern "C" {
#   include <tchar.h>
#   include <windows.h>
}
#else
#   error "Unsupported platform."
#endif

#include <map>
#include <string>

#include <boost/assert.hpp>
#include <boost/process/exceptions.hpp>
#include <boost/shared_array.hpp>
#include <boost/throw_exception.hpp>

#if defined(BOOST_PROCESS_POSIX_API)
extern "C" {
    extern char** environ;
};
#endif

namespace boost {
namespace process {
namespace detail {

// ------------------------------------------------------------------------

//!
//! \brief Representation of a process' environment variables.
//!
//! The environment class is a container that maps environment variable
//! names to values.  Given that it inherits from std::map\<\>, all
//! the operations supported by a standard map are also provided here.
//!
//! During initialization, the constructor grabs a snapshot of the current
//! process' environment and sets up the new object to contain all the
//! existing variables.  It should be clear, however, that modifications
//! made to one of these objects do not change the current process'
//! environment.
//!
//! Aside from the variable name to value mapping, this class also
//! provides convenience functions to generate the data formats required
//! to launch new processes under a controlled environment for the
//! different operating systems supported by the library.
//!
//! At last, note that the environment is sorted alphabetically.  This is
//! provided for-free in the map container and is required by Win32
//! systems.
//!
class environment :
    public std::map< std::string, std::string >
{
public:
    //!
    //! \brief Constructs a new environment object.
    //!
    //! This default constructor creates a new environment object that
    //! initially represents the current process' environment table.
    //! This table can be modified later on by the set() and unset()
    //! methods, although they do \b not modify the current environment.
    //!
    //! \see set() and unset().
    //!
    environment(void);

    //!
    //! \brief Sets an environment variable to the given value.
    //!
    //! Sets the \a var environment variable to the \a value.  If the
    //! variable already exists, its value is replaced by the new one.
    //!
    //! Although it may seem strange, \a var may be empty.  This is
    //! needed in Win32 systems to represent the process' work directory
    //! and hence needs to be set in the environment prior startup.
    //!
    //! Similarly, \a value can also be empty.  Be aware though that this
    //! is not portable.  Win32 treats a variable with an empty value the
    //! same as if it was undefined.  On the other hands, POSIX systems
    //! consider a variable with an empty value to be defined.
    //!
    //! \post The object is modified so that the \a var key is mapped
    //!       to the \a value value.
    //!
    void set(const std::string& var, const std::string& value);

    //!
    //! \brief Unsets an environment variable.
    //!
    //! Unsets the \a var environment variable.  It needn't exist in the
    //! environment.
    //!
    //! \post The object is modified so that the \a var key is not
    //!       mapped to any value.
    //!
    void unset(const std::string& var);

    //!
    //! \brief Returns a char** table to be used by execve().
    //!
    //! Converts the environment's contents to the format used by the
    //! execve() system call.  The returned char** array is allocated
    //! in dynamic memory; the caller must free it when not used any
    //! more.
    //!
    //! This function is theorically POSIX-specific but as it does not
    //! rely on any POSIX function, it is provided in all systems.
    //!
    //! \return A dynamically allocated char** array that represents
    //!         the environment's content.  Each array entry is a
    //!         null-terminated string of the form var=value.
    //!
    char** envp(void) const;

#if defined(BOOST_PROCESS_WIN32_API) || defined(BOOST_PROCESS_DOXYGEN)
    //!
    //! \brief Returns a string to be used by CreateProcess().
    //!
    //! Converst the environment's contents to the format used by the
    //! CreateProcess() system call.  The returned TCHAR* string is
    //! allocated in dynamic memory; the caller must free it when not
    //! used any more.  This is enforced by the use of a shared pointer.
    //!
    //! This function is only available in Win32 systems.
    //!
    //! \return A dynamically allocated TCHAR* string that represents
    //!         the environment's content.  This string is of the form
    //!         var1=value1\\0var2=value2\\0\\0.
    //!
    boost::shared_array< TCHAR > win32_strings(void) const;
#endif
};

// ------------------------------------------------------------------------

inline
environment::environment(void)
{
#if defined(BOOST_PROCESS_POSIX_API)
    char** ptr = ::environ;
    while (*ptr != NULL) {
        std::string str = *ptr;
        std::string::size_type pos = str.find('=');
        insert(value_type(str.substr(0, pos),
                          str.substr(pos + 1, str.length())));
        ptr++;
    }
#elif defined(BOOST_PROCESS_WIN32_API)
    TCHAR* es = ::GetEnvironmentStrings();
    if (es == NULL)
        boost::throw_exception
            (system_error("boost::process::detail::environment::environment",
                          "GetEnvironmentStrings failed", ::GetLastError()));

    try {
        while (*es != '\0') {
            std::string str = es;
            std::string::size_type pos = str.find('=');
            insert(value_type(str.substr(0, pos),
                              str.substr(pos + 1, str.length())));
            es += str.length() + 1;
        }
    } catch (...) {
        ::FreeEnvironmentStrings(es);
        throw;
    }

    ::FreeEnvironmentStrings(es);
#endif
}

// ------------------------------------------------------------------------

inline
void
environment::set(const std::string& var, const std::string& value)
{
    insert(value_type(var, value));
}

// ------------------------------------------------------------------------

inline
void
environment::unset(const std::string& var)
{
    erase(var);
}

// ------------------------------------------------------------------------

inline
char**
environment::envp(void)
    const
{
    char** ep = new char*[size() + 1];

    size_type i = 0;
    for (const_iterator iter = begin(); iter != end(); iter++) {
        std::string tmp = (*iter).first + "=" + (*iter).second;

        char* cstr = new char[tmp.length() + 1];
#if defined(BOOST_PROCESS_POSIX_API)
        std::strncpy(cstr, tmp.c_str(), tmp.length());
#elif defined(BOOST_PROCESS_WIN32_API)
        ::strcpy_s(cstr, tmp.length() + 1, tmp.c_str());
#endif
        cstr[tmp.length()] = '\0';

        ep[i++] = cstr;
    }

    ep[i] = NULL;

    return ep;
}

// ------------------------------------------------------------------------

#if defined(BOOST_PROCESS_WIN32_API) || defined(BOOST_PROCESS_DOXYGEN)
inline
boost::shared_array< TCHAR >
environment::win32_strings(void)
    const
{
    boost::shared_array< TCHAR > strs(NULL);

    if (size() == 0) {
        strs.reset(new TCHAR[2]);
        ::ZeroMemory(strs.get(), sizeof(TCHAR) * 2);
    } else {
        std::string::size_type len = sizeof(TCHAR);
        for (const_iterator iter = begin(); iter != end(); iter++)
            len += ((*iter).first.length() + 1 + (*iter).second.length() +
                    1) * sizeof(TCHAR);

        strs.reset(new TCHAR[len]);

        TCHAR* ptr = strs.get();
        for (const_iterator iter = begin(); iter != end(); iter++) {
            std::string tmp = (*iter).first + "=" + (*iter).second;
            _tcscpy_s(ptr, len - (ptr - strs.get()) * sizeof(TCHAR),
                      TEXT(tmp.c_str()));
            ptr += (tmp.length() + 1) * sizeof(TCHAR);

            BOOST_ASSERT(static_cast< std::string::size_type >
                (ptr - strs.get()) * sizeof(TCHAR) < len);
        }
        *ptr = '\0';
    }

    BOOST_ASSERT(strs.get() != NULL);
    return strs;
}
#endif

// ------------------------------------------------------------------------

} // namespace detail
} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_DETAIL_ENVIRONMENT_HPP)
